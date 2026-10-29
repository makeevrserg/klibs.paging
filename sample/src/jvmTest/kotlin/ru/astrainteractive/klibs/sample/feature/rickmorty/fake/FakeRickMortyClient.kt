package ru.astrainteractive.klibs.sample.feature.rickmorty.fake

import ru.astrainteractive.klibs.sample.feature.rickmorty.api.RickMortyClient
import ru.astrainteractive.klibs.sample.feature.rickmorty.model.Filter
import ru.astrainteractive.klibs.sample.feature.rickmorty.model.RickMortyCharacter

internal class FakeRickMortyClient : RickMortyClient {
    var result: Result<List<RickMortyCharacter>> = Result.success(emptyList())

    override suspend fun fetchCharacters(page: Int, pageSize: Int, filter: Filter): List<RickMortyCharacter> {
        return result.getOrThrow()
    }
}
