package me.bookk.feature.services.presentation.service.list

import me.bookk.core.presentation.navigation.NavigationDestination

sealed class ServiceListDestination : NavigationDestination() {
    data object Back : ServiceListDestination()
}