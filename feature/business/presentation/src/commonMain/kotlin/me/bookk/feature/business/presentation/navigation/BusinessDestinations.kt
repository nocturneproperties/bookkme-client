package me.bookk.feature.business.presentation.navigation

import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

sealed class BusinessDestination {
    @Serializable
    data object Dashboard : BusinessDestination()

    @Serializable
    data object Analytics : BusinessDestination()

    @Serializable
    data object Settings : BusinessDestination()

    @Serializable
    data class Plugins(val id: Uuid) : BusinessDestination()
}
