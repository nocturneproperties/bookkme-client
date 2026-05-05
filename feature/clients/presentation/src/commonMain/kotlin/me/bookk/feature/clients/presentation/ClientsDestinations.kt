package me.bookk.feature.clients.presentation

import me.bookk.core.presentation.navigation.NavigationDestination
import kotlin.uuid.Uuid

sealed class ClientsDestinations : NavigationDestination() {
    data class Clients(val id: Uuid) : ClientsDestinations()

    data class ClientDetails(val id: Uuid) : ClientsDestinations()

    data class CreateClient(val businessId: Uuid) : ClientsDestinations()
}