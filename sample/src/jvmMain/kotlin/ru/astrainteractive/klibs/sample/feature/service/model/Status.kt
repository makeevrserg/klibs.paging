package ru.astrainteractive.klibs.sample.feature.service.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal enum class Status(val string: String) {
    @SerialName("Alive")
    ALIVE("Alive"),

    @SerialName("Dead")
    DEAD("Dead"),

    @SerialName("unknown")
    UNKNOWN("unknown")
}
