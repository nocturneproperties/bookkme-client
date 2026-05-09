package me.bookk.feature.clients.presentation.list

import me.bookk.core.presentation.navigation.NavigationDestination
import kotlin.uuid.Uuid

sealed class ClientsListDestination : NavigationDestination() {
    data object Back : ClientsListDestination()
    data class AddClient(val businessId: Uuid) : ClientsListDestination()
    data class ClientDetails(val clientId: Uuid) : ClientsListDestination()
}