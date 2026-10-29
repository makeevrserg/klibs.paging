package ru.astrainteractive.klibs.sample.feature.rickmorty.api

import ru.astrainteractive.klibs.sample.feature.rickmorty.model.Filter
import ru.astrainteractive.klibs.sample.feature.rickmorty.model.RickMortyCharacter

internal interface RickMortyClient {
    suspend fun fetchCharacters(page: Int, pageSize: Int, filter: Filter): List<RickMortyCharacter>
}
