package ru.astrainteractive.klibs.sample.feature.rickmorty.ktor

import ru.astrainteractive.klibs.paging.collector.api.PagingCollector
import ru.astrainteractive.klibs.paging.collector.memory.DefaultPagingCollector
import ru.astrainteractive.klibs.paging.data.api.PagedListDataSource
import ru.astrainteractive.klibs.paging.state.model.PageResult
import ru.astrainteractive.klibs.paging.state.model.PagingState
import ru.astrainteractive.klibs.sample.feature.rickmorty.model.Filter
import ru.astrainteractive.klibs.sample.feature.rickmorty.model.RickMortyPageContext

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
