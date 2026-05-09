package me.bookk.feature.clients.presentation

import kotlinx.serialization.Serializable
import me.bookk.core.presentation.navigation.NavigationDestination
import kotlin.uuid.Uuid

sealed class ClientsDestinations : NavigationDestination() {
    @Serializable
    data class Clients(val id: Uuid) : ClientsDestinations()
    @Serializable
    data class ClientDetails(val id: Uuid) : ClientsDestinations()
    @Serializable
    data class CreateClient(val businessId: Uuid) : ClientsDestinations()
}