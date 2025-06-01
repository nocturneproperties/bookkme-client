package me.bookk.feature.business.presentation.navigation

import kotlinx.serialization.Serializable

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
    data object Clients : BusinessDestination()

    @Serializable
    data object Analytics : BusinessDestination()

    @Serializable
    data object Settings : BusinessDestination()
}
