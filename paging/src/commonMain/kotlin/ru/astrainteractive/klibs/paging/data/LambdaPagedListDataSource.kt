package ru.astrainteractive.klibs.paging.data

import ru.astrainteractive.klibs.paging.context.PageContext
import ru.astrainteractive.klibs.paging.state.PagingState

/**
 * This will allows you to use lambda data source
 */
class LambdaPagedListDataSource<T, K : PageContext>(
    private val loadPageLambda: suspend (PagingState<T, K>) -> Result<List<T>>
) : PagedListDataSource<T, K> {
    override suspend fun getListResult(pagingState: PagingState<T, K>): Result<List<T>> {
        return loadPageLambda.invoke(pagingState)
    }
}
