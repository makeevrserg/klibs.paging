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

    fun update(pagingState: (PagingState<K>) -> PagingState<K>)

    /**
     * Update current [listStateFlow]
     */
    fun submitList(list: List<T>)

    /**
     * Load next page if last page not reached
     */
    suspend fun loadNextPage()
}
