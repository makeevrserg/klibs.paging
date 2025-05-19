package ru.astrainteractive.klibs.paging.collector

import kotlinx.coroutines.flow.StateFlow
import ru.astrainteractive.klibs.paging.context.PageContext
import ru.astrainteractive.klibs.paging.state.PagingState

/**
 * Interface representing a collector that manages the state and lifecycle of paged data loading.
 *
 * @param T The type of items being loaded.
 * @param K The type of [PageContext] used for pagination.
 */
interface PagingCollector<T, K : PageContext> {

    /**
     * A [StateFlow] representing the current state of pagination.
     * Includes information such as loaded items, loading status, and errors.
     */
    val state: StateFlow<PagingState<T, K>>

    /**
     * Resets the pagination to the initial state and restarts loading from the beginning.
     * This is typically used when refreshing the data or resetting the context.
     */
    suspend fun resetToInitial()

    /**
     * Cancels any ongoing page loading operations and waits for their completion.
     * Ensures that any background tasks are safely terminated before proceeding.
     */
    suspend fun cancelAndJoin()

    /**
     * Updates the current [PagingState] using the provided transformation function.
     *
     * @param pagingState A lambda that receives the current state and returns a new one.
     */
    fun update(pagingState: (PagingState<T, K>) -> PagingState<T, K>)

    /**
     * Triggers loading of the next page based on the given [PageContext] factory.
     *
     * @param nextContext A suspending lambda that creates the next page context using a [PageContext.Factory].
     */
    suspend fun loadPage(nextContext: suspend (PageContext.Factory<K>) -> K)
}
