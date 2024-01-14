package ru.astrainteractive.klibs.paging

import ru.astrainteractive.klibs.paging.data.PagedListDataSource
import ru.astrainteractive.klibs.paging.descriptor.IntPageDescriptor
import ru.astrainteractive.klibs.paging.state.PagingState

class IntPagerCollector<T>(
    private val initialPage: Int = 0,
    private val pageSize: Int = 10,
    private val pager: PagedListDataSource<T, Int>,
) : PagingCollector<T, Int> by DefaultPagingCollector(
    initialPagingState = PagingState(
        pageDescriptor = IntPageDescriptor(page = initialPage),
        pageSizeAtLeast = pageSize,
        isLastPage = false,
        isLoading = false,
        isFailure = false
    ),
    pager = pager
)
