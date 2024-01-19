package ru.astrainteractive.klibs.sample.feature.service.model

import kotlinx.serialization.Serializable

@Serializable
internal class CharacterModel(
    val id: Long,
    val image: String,
    val name: String,
    val status: Status
)
