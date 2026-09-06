package me.bookk.feature.services.presentation

import kotlinx.serialization.Serializable
import me.bookk.core.presentation.navigation.NavigationDestination
import kotlin.uuid.Uuid

sealed class ServicesDestination : NavigationDestination() {
    @Serializable
    data object Services : ServicesDestination()

    @Serializable
    data class AddService(val businessId: Uuid) : ServicesDestination()

    @Serializable
    data object ServiceGroupList : ServicesDestination()
}