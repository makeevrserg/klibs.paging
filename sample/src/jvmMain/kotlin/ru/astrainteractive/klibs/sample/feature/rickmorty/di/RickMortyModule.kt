package ru.astrainteractive.klibs.sample.feature.rickmorty.di

import io.ktor.client.HttpClient
import kotlinx.serialization.json.Json
import ru.astrainteractive.klibs.sample.feature.rickmorty.api.RickMortyClient
import ru.astrainteractive.klibs.sample.feature.rickmorty.api.RickMortyRepository
import ru.astrainteractive.klibs.sample.feature.rickmorty.ktor.KtorRickMortyClient
import ru.astrainteractive.klibs.sample.feature.rickmorty.ktor.KtorRickMortyPagedListDataSource
import ru.astrainteractive.klibs.sample.feature.rickmorty.ktor.KtorRickMortyRepository
import ru.astrainteractive.klibs.sample.feature.rickmorty.ktor.RickMortyPagerCollector
import ru.astrainteractive.klibs.sample.feature.rickmorty.model.Filter

internal class RickMortyModule {
    private val json by lazy {
        Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        }
    }

    private val httpClient by lazy {
        HttpClient()
    }

    private val rickMortyClient: RickMortyClient by lazy {
        KtorRickMortyClient(
            httpClient = httpClient,
            json = json
        )
    }

    private val dataSource by lazy {
        KtorRickMortyPagedListDataSource(rickMortyClient)
    }

    private val pagingCollector by lazy {
        RickMortyPagerCollector(
            initialPage = 0,
            pager = dataSource,
            initialFilter = Filter()
        )
    }

    val rickMortyRepository: RickMortyRepository by lazy {
        KtorRickMortyRepository(pagingCollector)
    }
}
