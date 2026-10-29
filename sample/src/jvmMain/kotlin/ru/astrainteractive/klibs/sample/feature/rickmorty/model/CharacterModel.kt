package ru.astrainteractive.klibs.sample.feature.rickmorty.model

import kotlinx.serialization.Serializable

@Serializable
internal class CharacterModel(
    val id: Long,
    val image: String,
    val name: String,
    val status: Status
)
