package ru.astrainteractive.klibs.sample.feature.data.paging

import ru.astrainteractive.klibs.paging.collector.DefaultPagingCollector
import ru.astrainteractive.klibs.paging.collector.PagingCollector
import ru.astrainteractive.klibs.paging.data.PagedListDataSource
import ru.astrainteractive.klibs.paging.state.PageResult
import ru.astrainteractive.klibs.paging.state.PagingState
import ru.astrainteractive.klibs.sample.feature.service.model.Filter

internal class RickMortyPagerCollector<T>(
    private val initialPage: Int = 0,
    private val pager: PagedListDataSource<T, RickMortyPageContext>,
    private val initialFilter: Filter
) : PagingCollector<T, RickMortyPageContext> by DefaultPagingCollector(
    initialPagingStateFactory = {
        PagingState(
            pageContext = RickMortyPageContext(page = initialPage, filter = initialFilter),
            items = emptyList(),
            pageResult = PageResult.Pending
        )
    },
    pager = pager,
    pageContextFactory = RickMortyPageContext.Factory
)
