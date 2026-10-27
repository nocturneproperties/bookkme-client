package me.bookk.designsystem.uistate

import androidx.compose.runtime.mutableStateListOf
import me.bookk.core.presentation.navigation.NavigationDestination

class AndroidNavigationState<T : NavigationDestination> : NavigationState<T> {
    override val navigationDestination: MutableList<T> = mutableStateListOf()

    override fun push(destination: T) {
        navigationDestination.add(destination)
    }

    override fun removeFirst() {
        navigationDestination.removeAt(0)
    }
}