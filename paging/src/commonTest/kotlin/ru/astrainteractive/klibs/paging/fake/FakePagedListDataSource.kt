package ru.astrainteractive.klibs.paging.fake

import kotlinx.coroutines.delay
import ru.astrainteractive.klibs.paging.context.IntPageContext
import ru.astrainteractive.klibs.paging.data.PagedListDataSource
import ru.astrainteractive.klibs.paging.state.PagingState
import kotlin.time.Duration

/**
 * In-memory fake [PagedListDataSource] that serves fixed-size slices of [items], mimicking a real
 * page-number based backend.
 *
 * It behaves like an actual paginated endpoint: a page beyond the available data yields an empty
 * list, and the data returned depends solely on the requested [IntPageContext]. This lets tests
 * assert observable paging behavior instead of how often the source was queried.
 *
 * @property items The full backing dataset the source paginates over.
 * @property pageSize The number of items returned per page.
 * @property latency Artificial delay applied before each response, used to exercise in-flight states.
 */
class FakePagedListDataSource<T>(
    private val items: List<T>,
    private val pageSize: Int,
    private val latency: Duration = Duration.ZERO,
) : PagedListDataSource<T, IntPageContext> {

    /**
     * Simulates a backend outage. While non-null, every request fails with this error; set it back
     * to `null` to model the backend recovering.
     */
    var failure: Throwable? = null

    override suspend fun getListResult(
        pagingState: PagingState<T, IntPageContext>
    ): Result<List<T>> {
        delay(latency)
        failure?.let { error -> return Result.failure(error) }
        val offset = (pagingState.pageContext.page * pageSize).coerceAtLeast(0)
        return runCatching { items.drop(offset).take(pageSize) }
    }
}
