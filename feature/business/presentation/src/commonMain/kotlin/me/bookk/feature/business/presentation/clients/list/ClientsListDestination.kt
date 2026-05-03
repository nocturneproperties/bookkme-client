package me.bookk.feature.business.presentation.clients.list

import me.bookk.core.presentation.navigation.NavigationDestination
import me.bookk.feature.business.domain.api.entity.Client
import kotlin.uuid.Uuid

sealed class ClientsListDestination : NavigationDestination() {
    data object Back : ClientsListDestination()
    data class AddClient(val businessId: Uuid) : ClientsListDestination()
    data class ClientDetails(val client: Client) : ClientsListDestination()
}