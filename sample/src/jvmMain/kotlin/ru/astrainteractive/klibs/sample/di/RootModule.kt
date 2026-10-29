@file:Suppress("RootModulePlacement")

package ru.astrainteractive.klibs.sample.di

import ru.astrainteractive.klibs.sample.feature.rickmorty.di.RickMortyModule

internal class RootModule {
    val rickMortyModule by lazy { RickMortyModule() }
}
