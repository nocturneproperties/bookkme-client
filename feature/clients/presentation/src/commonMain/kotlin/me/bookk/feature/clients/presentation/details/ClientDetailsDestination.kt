package me.bookk.feature.clients.presentation.details

import me.bookk.core.presentation.navigation.NavigationDestination

sealed class ClientDetailsDestination : NavigationDestination() {
    data object Back : ClientDetailsDestination()
}