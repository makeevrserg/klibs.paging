package ru.astrainteractive.klibs.sample.feature.service

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import ru.astrainteractive.klibs.sample.feature.service.model.CharacterModel
import ru.astrainteractive.klibs.sample.feature.service.model.Filter

internal interface RickMortyService {
    suspend fun fetchCharacters(page: Int, pageSize: Int, filter: Filter): List<CharacterModel>
}

internal class RickMortyServiceImpl(
    private val httpClient: HttpClient,
    private val json: Json
) : RickMortyService {
    override suspend fun fetchCharacters(page: Int, pageSize: Int, filter: Filter): List<CharacterModel> {
        val stringBody = httpClient.get("https://rickandmortyapi.com/api/character/") {
            parameter("name", filter.name)
            parameter("status", filter.status?.string)
            parameter("page", page)
            parameter("count", pageSize)
        }.bodyAsText()
        val resultsString = json.parseToJsonElement(stringBody).jsonObject["results"]?.toString()
        return if (resultsString.isNullOrBlank()) {
            emptyList()
        } else {
            json.decodeFromString(resultsString)
        }
    }
}
