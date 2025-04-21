package me.bookk.feature.business.presentation.create

import me.bookk.core.presentation.navigation.NavigationDestination

sealed class CreateBusinessNavigationDestination : NavigationDestination() {
    data object Main : CreateBusinessNavigationDestination()
}