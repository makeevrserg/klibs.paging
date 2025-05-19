package ru.astrainteractive.klibs.paging.collector

import kotlinx.coroutines.flow.StateFlow
import ru.astrainteractive.klibs.paging.context.PageContext
import ru.astrainteractive.klibs.paging.state.PagingState

/**
 * This is a State Holder for your paging. Define it inside Repository or ViewModel
 */
interface PagingCollector<T, K : PageContext> {
    /**
     * Current state
     */
    val state: StateFlow<PagingState<T, K>>

    /**
     * Reset [state] to initial value and perform [cancelAndJoin]
     */
    suspend fun resetToInitial()

    /**
     * Cancels current [loadNextPage] job
     */
    suspend fun cancelAndJoin()

    /**
     * Update current [state]
     */
    fun update(pagingState: (PagingState<T, K>) -> PagingState<T, K>)

    /**
     * Load next page if last page not reached
     */
    suspend fun loadPage(nextContext: suspend (PageContext.Factory<K>) -> K)
}
