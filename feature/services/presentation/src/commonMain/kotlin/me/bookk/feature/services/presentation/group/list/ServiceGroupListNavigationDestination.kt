package me.bookk.feature.services.presentation.group.list

import me.bookk.core.presentation.navigation.NavigationDestination

sealed class ServiceGroupListDestination : NavigationDestination() {
    data object Back : ServiceGroupListDestination()
}
