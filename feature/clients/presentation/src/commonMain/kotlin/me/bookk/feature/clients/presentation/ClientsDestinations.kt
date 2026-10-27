package me.bookk.feature.clients.presentation

import kotlinx.serialization.Serializable
import me.bookk.core.presentation.navigation.NavigationDestination
import kotlin.uuid.Uuid

sealed class ClientsDestinations : NavigationDestination() {
    @Serializable
    data object Clients : ClientsDestinations()
    @Serializable
    data class ClientDetails(val id: Uuid) : ClientsDestinations()
    @Serializable
    data class CreateClient(val businessId: Uuid) : ClientsDestinations()
    @Serializable
    data class EditClient(val id: Uuid) : ClientsDestinations()
}