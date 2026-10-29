package ru.astrainteractive.klibs.sample.feature.rickmorty.ktor

import kotlinx.coroutines.flow.StateFlow
import ru.astrainteractive.klibs.paging.state.PageResult
import ru.astrainteractive.klibs.paging.state.PagingState
import ru.astrainteractive.klibs.paging.util.loadNextPage
import ru.astrainteractive.klibs.paging.util.updatePageContext
import ru.astrainteractive.klibs.sample.feature.rickmorty.api.RickMortyClient
import ru.astrainteractive.klibs.sample.feature.rickmorty.api.RickMortyRepository
import ru.astrainteractive.klibs.sample.feature.rickmorty.model.CharacterModel
import ru.astrainteractive.klibs.sample.feature.rickmorty.model.Filter
import ru.astrainteractive.klibs.sample.feature.rickmorty.model.RickMortyPageContext
import ru.astrainteractive.klibs.sample.feature.rickmorty.paging.RickMortyPagerCollector
import kotlin.coroutines.cancellation.CancellationException

internal class KtorRickMortyRepository(
    private val rickMortyClient: RickMortyClient
) : RickMortyRepository {

    private val pagingCollector = RickMortyPagerCollector(
        initialPage = 0,
        initialFilter = Filter(),
        pager = { pagingState ->
            runCatching {
                rickMortyClient.fetchCharacters(
                    page = pagingState.pageContext.page,
                    pageSize = 10,
                    filter = pagingState.pageContext.filter
                )
            }.onFailure { error ->
                if (error is CancellationException) throw error
            }
        }
    )

    override val pagingState: StateFlow<PagingState<CharacterModel, RickMortyPageContext>> = pagingCollector.state

    override fun reset() {
        pagingCollector.update { pagingState ->
            pagingState.copy(
                pageContext = pagingState.pageContext.copy(page = 0),
                items = emptyList(),
                pageResult = PageResult.Pending
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
