package me.bookk.feature.business.presentation.clients.list

import me.bookk.core.presentation.navigation.NavigationDestination
import me.bookk.feature.business.domain.api.entity.Client

sealed class ClientsListDestination : NavigationDestination() {
    data object AddClient : ClientsListDestination()
    data class ClientDetails(val client: Client) : ClientsListDestination()
}