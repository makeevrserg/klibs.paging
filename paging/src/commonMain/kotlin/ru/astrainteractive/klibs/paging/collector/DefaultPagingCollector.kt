package ru.astrainteractive.klibs.paging.collector

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import ru.astrainteractive.klibs.paging.context.PageContext
import ru.astrainteractive.klibs.paging.data.PagedListDataSource
import ru.astrainteractive.klibs.paging.state.PageResult
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
    private val mutex = Mutex()

    override val state: MutableStateFlow<PagingState<T, K>> = MutableStateFlow(initialPagingStateFactory.invoke())

    private suspend fun startJobWithMutex(block: suspend CoroutineScope.() -> Unit) {
        if (mutex.isLocked) return
        mutex.withLock {
            supervisorScope {
                latestJob?.join()
                latestJob = launch { block.invoke(this) }
                latestJob?.join()
                latestJob = null
            }
        }
    }

    override suspend fun resetToInitial() {
        state.emit(initialPagingStateFactory.invoke())
    }

    override suspend fun cancelAndJoin() {
        latestJob?.cancelAndJoin()
        latestJob = null
    }

    override fun update(pagingState: (PagingState<T, K>) -> PagingState<T, K>) {
        state.update { state -> pagingState.invoke(state) }
    }

    override suspend fun loadPage(nextContext: suspend (PageContext.Factory<K>) -> K) {
        startJobWithMutex {
            if (state.first().pageResult !is PageResult.Pending) return@startJobWithMutex

            state.update { pagingState -> pagingState.copy(pageResult = PageResult.Loading) }

            pager.getListResult(state.first())
                .onFailure { throwable ->
                    state.update { pagingState ->
                        pagingState.copy(pageResult = PageResult.Failure(throwable))
                    }
                }
                .onSuccess { newList ->
                    when {
                        newList.isEmpty() -> {
                            state.update { pagingState -> pagingState.copy(pageResult = PageResult.LastPage) }
                        }

                        newList.isNotEmpty() -> {
                            state.update { pagingState ->
                                pagingState.copy(
                                    pageContext = nextContext.invoke(pageContextFactory),
                                    items = pagingState.items + newList,
                                    pageResult = PageResult.Pending,
                                )
                            }
                        }
                    }
                }
        }
    }
}
