package me.bookk.feature.authorization.presentation.bootstrap

import me.bookk.core.presentation.navigation.NavigationDestination

sealed class BootstrapNavigationDestination : NavigationDestination() {
    data object ToMain : BootstrapNavigationDestination()
    data object ToLogin : BootstrapNavigationDestination()
}