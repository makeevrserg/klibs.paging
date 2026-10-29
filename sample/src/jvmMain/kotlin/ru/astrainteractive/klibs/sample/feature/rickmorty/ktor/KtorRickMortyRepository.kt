package ru.astrainteractive.klibs.sample.feature.rickmorty.ktor

import kotlinx.coroutines.flow.StateFlow
import ru.astrainteractive.klibs.paging.state.model.PageResult
import ru.astrainteractive.klibs.paging.state.model.PagingState
import ru.astrainteractive.klibs.paging.util.loadNextPage
import ru.astrainteractive.klibs.paging.util.updatePageContext
import ru.astrainteractive.klibs.sample.feature.rickmorty.api.RickMortyRepository
import ru.astrainteractive.klibs.sample.feature.rickmorty.model.Filter
import ru.astrainteractive.klibs.sample.feature.rickmorty.model.RickMortyCharacter
import ru.astrainteractive.klibs.sample.feature.rickmorty.model.RickMortyPageContext

internal class KtorRickMortyRepository(
    private val pagingCollector: RickMortyPagerCollector<RickMortyCharacter>
) : RickMortyRepository {

    override val pagingState: StateFlow<PagingState<RickMortyCharacter, RickMortyPageContext>> = pagingCollector.state

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
