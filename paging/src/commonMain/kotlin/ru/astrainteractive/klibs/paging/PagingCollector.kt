package ru.astrainteractive.klibs.paging

import kotlinx.coroutines.flow.StateFlow
import ru.astrainteractive.klibs.paging.context.PageContext
import ru.astrainteractive.klibs.paging.state.PagingState

interface PagingCollector<T, K : PageContext> {
    val state: StateFlow<PagingState<T, K>>

    /**
     * Reset [state] [PagingState] to initial value
     */
    fun reset()

    /**
     * Update current [state]
     */
    fun update(pagingState: (PagingState<T, K>) -> PagingState<T, K>)

    /**
     * Load next page if last page not reached
     */
    suspend fun loadNextPage()
}
