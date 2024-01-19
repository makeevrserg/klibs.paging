package ru.astrainteractive.klibs.sample.feature.data

import kotlinx.coroutines.flow.StateFlow
import ru.astrainteractive.klibs.paging.state.PagingState
import ru.astrainteractive.klibs.sample.feature.data.paging.RickMortyPageContext
import ru.astrainteractive.klibs.sample.feature.service.model.CharacterModel
import ru.astrainteractive.klibs.sample.feature.service.model.Filter

/**
 * Your custom repository implementation
 */
internal interface RickMortyRepository {
    /**
     * Define stateflow of your pager
     */
    val pagingState: StateFlow<PagingState<CharacterModel, RickMortyPageContext>>

    /**
     * Add ability to reset it
     */
    fun reset()

    /**
     * Update your custom filter
     */
    fun updateFilter(filter: Filter)

    /**
     * Add ability to load next page
     */
    suspend fun loadNextPage()
}
