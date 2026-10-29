package ru.astrainteractive.klibs.sample.feature.rickmorty.api

import kotlinx.coroutines.flow.StateFlow
import ru.astrainteractive.klibs.paging.state.model.PagingState
import ru.astrainteractive.klibs.sample.feature.rickmorty.model.Filter
import ru.astrainteractive.klibs.sample.feature.rickmorty.model.RickMortyCharacter
import ru.astrainteractive.klibs.sample.feature.rickmorty.model.RickMortyPageContext

internal interface RickMortyRepository {
    val pagingState: StateFlow<PagingState<RickMortyCharacter, RickMortyPageContext>>

    fun reset()

    fun updateFilter(filter: Filter)

    suspend fun loadNextPage()
}
