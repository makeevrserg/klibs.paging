package ru.astrainteractive.klibs.sample.feature.rickmorty.fake

import ru.astrainteractive.klibs.sample.feature.rickmorty.api.RickMortyClient
import ru.astrainteractive.klibs.sample.feature.rickmorty.model.CharacterModel
import ru.astrainteractive.klibs.sample.feature.rickmorty.model.Filter

internal class FakeRickMortyClient : RickMortyClient {
    var result: Result<List<CharacterModel>> = Result.success(emptyList())

    override suspend fun fetchCharacters(page: Int, pageSize: Int, filter: Filter): List<CharacterModel> {
        return result.getOrThrow()
    }
}
