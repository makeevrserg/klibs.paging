package ru.astrainteractive.klibs.sample.feature.data

import kotlinx.coroutines.flow.StateFlow
import ru.astrainteractive.klibs.paging.state.PagingState
import ru.astrainteractive.klibs.paging.util.PagingCollectorExt.updatePageContext
import ru.astrainteractive.klibs.sample.feature.data.paging.RickMortyPageContext
import ru.astrainteractive.klibs.sample.feature.data.paging.RickMortyPagerCollector
import ru.astrainteractive.klibs.sample.feature.service.RickMortyService
import ru.astrainteractive.klibs.sample.feature.service.model.CharacterModel
import ru.astrainteractive.klibs.sample.feature.service.model.Filter

internal class RickMortyRepositoryImpl(
    private val rickMortyService: RickMortyService
) : RickMortyRepository {

    /**
     * Define your paging collector
     */
    private val pagingCollector = RickMortyPagerCollector(
        initialPage = 0,
        initialFilter = Filter(),
        pager = LambdaPagedListDataSource {
            runCatching {
                rickMortyService.fetchCharacters(
                    page = it.pageContext.page,
                    pageSize = it.pageSizeAtLeast,
                    filter = it.pageContext.filter
                )
            }.onFailure(Throwable::printStackTrace)
        }
    )

    override val pagingState: StateFlow<PagingState<CharacterModel, RickMortyPageContext>> = pagingCollector.state

    override fun reset() {
        pagingCollector.update { pagingState ->
            pagingState.copy(
                pageContext = pagingState.pageContext.copy(page = 0),
                items = emptyList(),
                isLoading = false,
                isLastPage = false,
                isFailure = false,
            )
        }
    }

    override fun updateFilter(filter: Filter) {
        pagingCollector.updatePageContext { pageContext -> pageContext.copy(filter = filter) }
    }

    override suspend fun loadNextPage() {
        pagingCollector.loadNextPage()
    }
}
