package ru.astrainteractive.klibs.sample.feature.rickmorty.model

import kotlinx.serialization.Serializable

@Serializable
internal data class Filter(
    val name: String? = null,
    val status: Status? = null
)
