package ru.astrainteractive.klibs.paging

import kotlinx.coroutines.flow.StateFlow
import ru.astrainteractive.klibs.paging.state.PagingState

interface PagingCollector<T, K : Any> {
    val pagingStateFlow: StateFlow<PagingState<K>>
    val listStateFlow: StateFlow<List<T>>

    /**
     * Reset [pagingStateFlow] and clear [listStateFlow]
     */
    fun reset()

    /**
     * Load next page if last page not reached
     */
    suspend fun loadNextPage()
}
