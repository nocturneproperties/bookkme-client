package me.bookk.designsystem.uistate

import me.bookk.core.presentation.navigation.NavigationDestination

interface NavigationState<T : NavigationDestination> {
    val navigationDestination: List<T>

    fun push(destination: T)
    fun removeFirst()
}