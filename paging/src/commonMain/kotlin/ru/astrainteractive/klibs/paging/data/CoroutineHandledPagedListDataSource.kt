package ru.astrainteractive.klibs.paging.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.astrainteractive.klibs.paging.context.PageContext
import ru.astrainteractive.klibs.paging.state.PagingState
import kotlin.coroutines.CoroutineContext

/**
 * This [PagedListDataSource] will add [runCatching] and [withContext]
 */
class CoroutineHandledPagedListDataSource<T, K : PageContext>(
    private val context: CoroutineContext = Dispatchers.Unconfined,
    private val onFailure: (Throwable) -> Unit = {},
    private val loadPageLambda: suspend (PagingState<T, K>) -> List<T>,
) : PagedListDataSource<T, K> {
    override suspend fun getListResult(pagingState: PagingState<T, K>): Result<List<T>> {
        return withContext(context) {
            kotlin.runCatching { loadPageLambda.invoke(pagingState) }.onFailure(onFailure)
        }
    }
}
