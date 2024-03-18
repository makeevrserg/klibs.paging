package ru.astrainteractive.klibs.paging

import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.job
import ru.astrainteractive.klibs.paging.context.PageContext
import ru.astrainteractive.klibs.paging.data.PagedListDataSource
import ru.astrainteractive.klibs.paging.state.PagingState

/**
 * This is a default implementation to be delegated from, but you can create your own
 */
class DefaultPagingCollector<T, K : PageContext>(
    private val initialPagingStateFactory: () -> PagingState<T, K>,
    private val pager: PagedListDataSource<T, K>,
    private val pageContextFactory: PageContext.Factory<K>
) : PagingCollector<T, K> {

    private var latestJob: Job? = null

    override val state: MutableStateFlow<PagingState<T, K>> = MutableStateFlow(initialPagingStateFactory.invoke())

    override suspend fun resetAndJoin() {
        cancelAndJoin()
        update { initialPagingStateFactory.invoke() }
    }

    override suspend fun cancelAndJoin() {
        latestJob?.cancelAndJoin()
        latestJob = null
    }

    override fun update(pagingState: (PagingState<T, K>) -> PagingState<T, K>) {
        val nextPagingState = pagingState.invoke(state.value)
        state.value = nextPagingState
    }

    override suspend fun loadNextPage() = coroutineScope {
        latestJob?.join()
        latestJob = coroutineContext.job
        if (state.value.isLastPage) return@coroutineScope
        if (state.value.isLoading) return@coroutineScope
        if (state.value.isFailure) return@coroutineScope

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
