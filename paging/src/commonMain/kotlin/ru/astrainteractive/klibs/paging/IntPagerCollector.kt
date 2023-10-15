package ru.astrainteractive.klibs.paging

import ru.astrainteractive.klibs.paging.data.PagedListDataSource
import ru.astrainteractive.klibs.paging.state.IntPagingState

class IntPagerCollector<T>(
    private val initialPage: Int = 0,
    private val pageSize: Int = 10,
    private val pager: PagedListDataSource<T, Int>,
) : PagingCollector<T, Int> by DefaultPagingCollector(
    initialPagingState = IntPagingState(
        pageDescriptor = initialPage,
        pageSizeAtLeast = pageSize
    ),
    pager = pager
)
