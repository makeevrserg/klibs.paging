package ru.astrainteractive.klibs.paging.data

import ru.astrainteractive.klibs.paging.context.PageContext
import ru.astrainteractive.klibs.paging.state.PagingState

/**
 * This interface is required to fetch page items from your service depending on current [PagingState]
 */
interface PagedListDataSource<T, K : PageContext> {
    suspend fun getListResult(pagingState: PagingState<T, K>): Result<List<T>>
}
