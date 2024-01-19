package ru.astrainteractive.klibs.sample.di

import io.ktor.client.HttpClient
import kotlinx.serialization.json.Json
import ru.astrainteractive.klibs.sample.feature.data.RickMortyRepository
import ru.astrainteractive.klibs.sample.feature.data.RickMortyRepositoryImpl
import ru.astrainteractive.klibs.sample.feature.service.RickMortyService
import ru.astrainteractive.klibs.sample.feature.service.RickMortyServiceImpl

internal interface ServicesModule {
    val json: Json
    val httpClient: HttpClient
    val rickMortyService: RickMortyService
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

        override val rickMortyService: RickMortyService by lazy {
            RickMortyServiceImpl(
                httpClient = httpClient,
                json = json
            )
        }

        override val rickMortyRepository: RickMortyRepository by lazy {
            RickMortyRepositoryImpl(rickMortyService)
        }
    }
}
