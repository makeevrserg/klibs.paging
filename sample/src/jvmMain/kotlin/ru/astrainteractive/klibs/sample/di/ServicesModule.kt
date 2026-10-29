package ru.astrainteractive.klibs.sample.di

import io.ktor.client.HttpClient
import kotlinx.serialization.json.Json
import ru.astrainteractive.klibs.sample.feature.rickmorty.api.RickMortyClient
import ru.astrainteractive.klibs.sample.feature.rickmorty.api.RickMortyRepository
import ru.astrainteractive.klibs.sample.feature.rickmorty.ktor.KtorRickMortyClient
import ru.astrainteractive.klibs.sample.feature.rickmorty.ktor.KtorRickMortyRepository

internal interface ServicesModule {
    val json: Json
    val httpClient: HttpClient
    val rickMortyClient: RickMortyClient
    val rickMortyRepository: RickMortyRepository

    class Default : ServicesModule {
        override val json by lazy {
            Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            }
        }

        override val httpClient by lazy {
            HttpClient {
//                install(ContentNegotiation) {
//                    json(jsonConfiguration)
//                }
            }
        }

        override val rickMortyClient: RickMortyClient by lazy {
            KtorRickMortyClient(
                httpClient = httpClient,
                json = json
            )
        }

        override val rickMortyRepository: RickMortyRepository by lazy {
            KtorRickMortyRepository(rickMortyClient)
        }
    }
}
