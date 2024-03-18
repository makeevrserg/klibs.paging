package ru.astrainteractive.klibs.paging

import ru.astrainteractive.klibs.paging.context.IntPageContext
import ru.astrainteractive.klibs.paging.data.PagedListDataSource
import ru.astrainteractive.klibs.paging.state.PagingState

/**
 * This is a default implementation for Integer page. You can define your own by delegation just like that
 */
class IntPagerCollector<T>(
    private val initialPage: Int = 0,
    private val pageSize: Int = 10,
    private val pager: PagedListDataSource<T, IntPageContext>,
) : PagingCollector<T, IntPageContext> by DefaultPagingCollector(
    initialPagingStateFactory = {
        PagingState(
            pageContext = IntPageContext(page = initialPage),
            items = emptyList<T>(),
            pageSizeAtLeast = pageSize,
            isLastPage = false,
            isLoading = false,
            isFailure = false
        )
    },
    pager = pager,
    pageContextFactory = IntPageContext.Factory
)
