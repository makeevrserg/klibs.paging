package ru.astrainteractive.klibs.paging

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import ru.astrainteractive.klibs.paging.context.PageContext
import ru.astrainteractive.klibs.paging.data.PagedListDataSource
import ru.astrainteractive.klibs.paging.state.PagingState

class DefaultPagingCollector<T, K : PageContext>(
    private val initialPagingState: PagingState<K>,
    private val pager: PagedListDataSource<T, K>,
    private val pageContextFactory: PageContext.Factory<K>
) : PagingCollector<T, K> {
    override val pagingStateFlow: MutableStateFlow<PagingState<K>> = MutableStateFlow(initialPagingState)
    override val listStateFlow = MutableStateFlow<List<T>>(emptyList())

    /**
     * Reset will return [pagingStateFlow] to [initialPagingState] and clear [listStateFlow] content
     */
    override fun reset() {
        listStateFlow.value = emptyList()
        pagingStateFlow.value = initialPagingState
    }

    override fun update(pagingState: (PagingState<K>) -> PagingState<K>) {
        val nextPagingState = pagingState.invoke(pagingStateFlow.value)
        pagingStateFlow.value = nextPagingState
    }

    override fun submitList(list: List<T>) {
        listStateFlow.value = list
    }

    override suspend fun loadNextPage() {
        if (pagingStateFlow.value.isLastPage) return
        if (pagingStateFlow.value.isLoading) return

        pagingStateFlow.update { pagingState ->
            pagingState.copy(isLoading = true)
        }

        val result = pager.getListResult(pagingStateFlow.value)

        result.onFailure {
            pagingStateFlow.update { pagingState ->
                pagingState.copy(isFailure = true)
            }
        }

        result.onSuccess { newList ->
            when {
                newList.isEmpty() -> {
                    pagingStateFlow.update { pagingState ->
                        pagingState.copy(isLastPage = true)
                    }
                }

                newList.isNotEmpty() -> {
                    pagingStateFlow.update { pagingState ->
                        pagingState.copy(
                            pageContext = pageContextFactory.next(pagingState.pageContext),
                            isLastPage = newList.size < pagingState.pageSizeAtLeast
                        )
                    }
                    listStateFlow.update { currentList ->
                        currentList + newList
                    }
                }
            }
        }

        pagingStateFlow.update { pagingState ->
            pagingState.copy(isLoading = false)
        }
    }
}
