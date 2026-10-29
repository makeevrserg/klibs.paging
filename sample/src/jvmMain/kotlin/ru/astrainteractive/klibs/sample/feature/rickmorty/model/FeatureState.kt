package ru.astrainteractive.klibs.sample.feature.rickmorty.model

internal data class FeatureState(
    val characters: List<RickMortyCharacter>,
    val filter: Filter,
    val isLastPage: Boolean,
    val isLoading: Boolean,
    val isError: Boolean
)
