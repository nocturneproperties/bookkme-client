package me.bookk.feature.business.presentation.navigation

import kotlinx.serialization.Serializable

sealed class BusinessDestination {
    @Serializable
    data object Example : BusinessDestination()
}
