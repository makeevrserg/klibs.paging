package ru.astrainteractive.klibs.paging.data

import ru.astrainteractive.klibs.paging.state.PagingState


/**
 * This will allows you to use lambda data source
 */
class LambdaPagedListDataSource<T, K : Any>(
    private val loadPageLambda: suspend (PagingState<K>) -> Result<List<T>>
) : PagedListDataSource<T, K> {
    override suspend fun getListResult(pagingState: PagingState<K>): Result<List<T>> {
        return loadPageLambda(pagingState)
    }
}