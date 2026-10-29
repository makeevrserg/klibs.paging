package ru.astrainteractive.klibs.sample.feature.rickmorty.ktor

import ru.astrainteractive.klibs.mikro.core.coroutines.propagateCancellationException
import ru.astrainteractive.klibs.paging.data.api.PagedListDataSource
import ru.astrainteractive.klibs.paging.state.model.PagingState
import ru.astrainteractive.klibs.sample.feature.rickmorty.api.RickMortyClient
import ru.astrainteractive.klibs.sample.feature.rickmorty.model.RickMortyCharacter
import ru.astrainteractive.klibs.sample.feature.rickmorty.model.RickMortyPageContext

internal class KtorRickMortyPagedListDataSource(
    private val rickMortyClient: RickMortyClient
) : PagedListDataSource<RickMortyCharacter, RickMortyPageContext> {
    override suspend fun getListResult(
        pagingState: PagingState<RickMortyCharacter, RickMortyPageContext>
    ): Result<List<RickMortyCharacter>> {
        return runCatching {
            rickMortyClient.fetchCharacters(
                page = pagingState.pageContext.page,
                pageSize = 10,
                filter = pagingState.pageContext.filter
            )
        }.propagateCancellationException()
    }
}
