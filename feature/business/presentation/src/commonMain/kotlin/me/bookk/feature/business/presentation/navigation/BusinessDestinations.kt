package me.bookk.feature.business.presentation.navigation

import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

sealed class BusinessDestination {
    @Serializable
    data object BlockingProgress : BusinessDestination()

    @Serializable
    data object Create : BusinessDestination()

    @Serializable
    data object Dashboard : BusinessDestination()

    @Serializable
    data object Employees : BusinessDestination()

    @Serializable
    data class Clients(val id: Uuid) : BusinessDestination()

    @Serializable
    data class ClientDetails(val id: Uuid) : BusinessDestination()

    @Serializable
    data class CreateClient(val businessId: Uuid) : BusinessDestination()

    @Serializable
    data object Analytics : BusinessDestination()

    @Serializable
    data class Settings(val id: Uuid) : BusinessDestination()
}
