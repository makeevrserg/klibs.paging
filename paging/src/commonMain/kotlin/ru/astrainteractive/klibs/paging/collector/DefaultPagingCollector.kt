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
 * Default implementation of [PagingCollector] that manages paging state and orchestrates
 * loading of data through a [PagedListDataSource].
 *
 * This class provides core paging behavior including maintaining state, handling page loading,
 * and supporting custom pagination strategies via a [PageContext.Factory].
 *
 * @param T The type of items being paged.
 * @param K The type of [PageContext] used to represent paging position.
 * @param initialPagingStateFactory A factory function that provides the initial [PagingState].
 * @param pager The data source responsible for loading paged data.
 * @param pageContextFactory A factory used to generate instances of [PageContext] for pagination.
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
