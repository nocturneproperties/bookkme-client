package me.bookk.feature.services.presentation

import kotlinx.serialization.Serializable
import me.bookk.core.presentation.navigation.NavigationDestination
import kotlin.uuid.Uuid

sealed class ServicesDestination : NavigationDestination() {
    @Serializable
    data class Services(val id: Uuid) : ServicesDestination()

    @Serializable
    data class AddService(val businessId: Uuid) : ServicesDestination()
}