package ru.astrainteractive.klibs.paging.collector

import ru.astrainteractive.klibs.paging.context.IntPageContext
import ru.astrainteractive.klibs.paging.data.PagedListDataSource
import ru.astrainteractive.klibs.paging.state.PageResult
import ru.astrainteractive.klibs.paging.state.PagingState

/**
 * This is a default implementation for Integer page. You can define your own by delegation just like that
 */
class IntPagerCollector<T>(
    private val initialPage: Int = 0,
    private val pager: PagedListDataSource<T, IntPageContext>,
) : PagingCollector<T, IntPageContext> by DefaultPagingCollector(
    initialPagingStateFactory = {
        PagingState(
            pageContext = IntPageContext(page = initialPage),
            items = emptyList(),
            pageResult = PageResult.Pending
        )
    },
    pager = pager,
    pageContextFactory = IntPageContext.Factory
)
