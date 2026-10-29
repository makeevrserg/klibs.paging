package ru.astrainteractive.klibs.sample.feature.rickmorty.api

import kotlinx.coroutines.flow.StateFlow
import ru.astrainteractive.klibs.paging.state.PagingState
import ru.astrainteractive.klibs.sample.feature.rickmorty.model.CharacterModel
import ru.astrainteractive.klibs.sample.feature.rickmorty.model.Filter
import ru.astrainteractive.klibs.sample.feature.rickmorty.model.RickMortyPageContext

internal interface RickMortyRepository {
    val pagingState: StateFlow<PagingState<CharacterModel, RickMortyPageContext>>

    fun reset()

    fun updateFilter(filter: Filter)

    suspend fun loadNextPage()
}
