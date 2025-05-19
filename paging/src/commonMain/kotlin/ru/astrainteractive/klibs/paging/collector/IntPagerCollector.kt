package ru.astrainteractive.klibs.paging.collector

import ru.astrainteractive.klibs.paging.context.IntPageContext
import ru.astrainteractive.klibs.paging.context.PageContext
import ru.astrainteractive.klibs.paging.data.PagedListDataSource
import ru.astrainteractive.klibs.paging.state.PageResult
import ru.astrainteractive.klibs.paging.state.PagingState

/**
 * An implementation of [PagingCollector] for paging based on integer page numbers.
 *
 * This class delegates all paging logic to [DefaultPagingCollector], initializing it with
 * an integer-based [PageContext] and an initial page.
 *
 * @param T The type of items being loaded.
 * @param initialPage The starting page index. Defaults to 0.
 * @param pager The data source responsible for loading pages based on [IntPageContext].
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
