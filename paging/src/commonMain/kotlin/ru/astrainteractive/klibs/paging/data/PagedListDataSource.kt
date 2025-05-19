package ru.astrainteractive.klibs.paging.data

import ru.astrainteractive.klibs.paging.context.PageContext
import ru.astrainteractive.klibs.paging.state.PagingState

/**
 * A functional interface representing a data source capable of loading a list of items
 * for a given [PagingState].
 *
 * @param T The type of items to be loaded.
 * @param K The type of [PageContext] used for pagination.
 */
fun interface PagedListDataSource<T, K : PageContext> {

    /**
     * Loads a list of items based on the provided [pagingState].
     *
     * @param pagingState The current state of pagination, including the context and previously loaded items.
     * @return A [Result] containing the loaded list of items, or an error if the operation fails.
     */
    suspend fun getListResult(pagingState: PagingState<T, K>): Result<List<T>>
}
