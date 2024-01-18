package ru.astrainteractive.klibs.paging

import kotlinx.coroutines.flow.StateFlow
import ru.astrainteractive.klibs.paging.context.PageContext
import ru.astrainteractive.klibs.paging.state.PagingState

interface PagingCollector<T, K : PageContext> {
    val pagingStateFlow: StateFlow<PagingState<K>>
    val listStateFlow: StateFlow<List<T>>

    /**
     * Reset [pagingStateFlow] and clear [listStateFlow]
     */
    fun reset()

    /**
     * Update current [listStateFlow]
     */
    fun submitList(list: List<T>)

    /**
     * Force update page context
     */
    fun updatePageContext(pageContext: K)

    /**
     * Load next page if last page not reached
     */
    suspend fun loadNextPage()
}
