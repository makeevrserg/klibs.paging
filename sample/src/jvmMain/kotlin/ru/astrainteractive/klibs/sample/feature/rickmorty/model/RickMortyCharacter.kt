package ru.astrainteractive.klibs.sample.feature.rickmorty.model

import kotlinx.serialization.Serializable

@Serializable
internal class RickMortyCharacter(
    val id: Long,
    val image: String,
    val name: String,
    val status: Status
)
