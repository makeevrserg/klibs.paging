package ru.astrainteractive.klibs.paging

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import ru.astrainteractive.klibs.paging.context.PageContext
import ru.astrainteractive.klibs.paging.data.PagedListDataSource
import ru.astrainteractive.klibs.paging.state.PagingState

class DefaultPagingCollector<T, K : PageContext>(
    private val initialPagingState: PagingState<T, K>,
    private val pager: PagedListDataSource<T, K>,
    private val pageContextFactory: PageContext.Factory<K>
) : PagingCollector<T, K> {
    override val state: MutableStateFlow<PagingState<T, K>> = MutableStateFlow(initialPagingState)

    /**
     * Reset will return [state] to [initialPagingState] and clear [listStateFlow] content
     */
    override fun reset() {
        update { initialPagingState }
    }

    override fun update(pagingState: (PagingState<T, K>) -> PagingState<T, K>) {
        val nextPagingState = pagingState.invoke(state.value)
        state.value = nextPagingState
    }

    override suspend fun loadNextPage() {
        if (state.value.isLastPage) return
        if (state.value.isLoading) return

        state.update { pagingState ->
            pagingState.copy(isLoading = true)
        }

        val result = pager.getListResult(state.value)

        result.onFailure {
            state.update { pagingState ->
                pagingState.copy(isFailure = true)
            }
        }

        result.onSuccess { newList ->
            when {
                newList.isEmpty() -> {
                    state.update { pagingState ->
                        pagingState.copy(isLastPage = true)
                    }
                }

                newList.isNotEmpty() -> {
                    state.update { pagingState ->
                        pagingState.copy(
                            pageContext = pageContextFactory.next(pagingState.pageContext),
                            isLastPage = newList.size < pagingState.pageSizeAtLeast,
                            items = pagingState.items + newList
                        )
                    }
                }
            }
        }

        state.update { pagingState ->
            pagingState.copy(isLoading = false)
        }
    }
}
