package ru.astrainteractive.klibs.paging.data

import ru.astrainteractive.klibs.paging.context.PageContext
import ru.astrainteractive.klibs.paging.state.PagingState

/**
 * Default paged list interface
 */
interface PagedListDataSource<T, K : PageContext> {
    suspend fun getListResult(pagingState: PagingState<T, K>): Result<List<T>>
}
