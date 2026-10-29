package ru.astrainteractive.klibs.sample.feature.rickmorty.api

import ru.astrainteractive.klibs.sample.feature.rickmorty.model.CharacterModel
import ru.astrainteractive.klibs.sample.feature.rickmorty.model.Filter

internal interface RickMortyClient {
    suspend fun fetchCharacters(page: Int, pageSize: Int, filter: Filter): List<CharacterModel>
}
