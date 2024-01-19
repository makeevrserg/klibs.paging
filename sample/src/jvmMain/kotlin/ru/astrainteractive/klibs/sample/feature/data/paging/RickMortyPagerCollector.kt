package ru.astrainteractive.klibs.sample.feature.data.paging

import ru.astrainteractive.klibs.paging.DefaultPagingCollector
import ru.astrainteractive.klibs.paging.PagingCollector
import ru.astrainteractive.klibs.paging.data.PagedListDataSource
import ru.astrainteractive.klibs.paging.state.PagingState
import ru.astrainteractive.klibs.sample.feature.service.model.Filter

internal class RickMortyPagerCollector<T>(
    private val initialPage: Int = 0,
    private val pageSize: Int = 10,
    private val pager: PagedListDataSource<T, RickMortyPageContext>,
    private val initialFilter: Filter
) : PagingCollector<T, RickMortyPageContext> by DefaultPagingCollector(
    initialPagingState = PagingState(
        pageContext = RickMortyPageContext(page = initialPage, filter = initialFilter),
        pageSizeAtLeast = pageSize,
        isLastPage = false,
        isLoading = false,
        isFailure = false,
        items = emptyList()
    ),
    pager = pager,
    pageContextFactory = RickMortyPageContext.Factory
)
