package ru.astrainteractive.klibs.paging

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import ru.astrainteractive.klibs.paging.data.PagedListDataSource
import ru.astrainteractive.klibs.paging.state.PagingState

class DefaultPagingCollector<T, K : Any>(
    private val initialPagingState: PagingState<K>,
    private val pager: PagedListDataSource<T, K>,
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

    override fun submitList(list: List<T>) {
        listStateFlow.value = list
    }

    override suspend fun loadNextPage() {
        if (pagingStateFlow.value.isLastPage) return
        if (pagingStateFlow.value.isLoading) return

        pagingStateFlow.update { pagingState ->
            pagingState.copyPagingState(isLoading = true)
        }

        val result = pager.getListResult(pagingStateFlow.value)

        result.onFailure {
            pagingStateFlow.update { pagingState ->
                pagingState.copyPagingState(isFailure = true)
            }
        }

        result.onSuccess { newList ->
            when {
                newList.isEmpty() -> {
                    pagingStateFlow.update { pagingState ->
                        pagingState.copyPagingState(isLastPage = true)
                    }
                }

                newList.isNotEmpty() -> {
                    pagingStateFlow.update { pagingState ->
                        pagingState.copyPagingState(
                            pageDescriptor = pagingStateFlow.value.createNextPageDescriptor(),
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
            pagingState.copyPagingState(isLoading = false)
        }
    }
}
