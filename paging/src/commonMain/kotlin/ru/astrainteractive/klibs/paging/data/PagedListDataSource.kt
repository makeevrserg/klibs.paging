package ru.astrainteractive.klibs.paging.data

import ru.astrainteractive.klibs.paging.state.PagingState

/**
 * Default paged list interface
 */
interface PagedListDataSource<T, K : Any> {
    suspend fun getListResult(pagingState: PagingState<K>): Result<List<T>>
}