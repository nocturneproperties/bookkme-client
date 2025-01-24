package me.bookk.designsystem.uistate

import me.bookk.core.presentation.navigation.NavigationDestination

interface NavigationState<T : NavigationDestination> {
    var navigationDestination: T?
}