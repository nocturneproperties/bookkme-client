package me.bookk.designsystem.uistate

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import me.bookk.core.presentation.navigation.NavigationDestination

class AndroidNavigationState<T : NavigationDestination> : NavigationState<T> {
    override var navigationDestination: T? by mutableStateOf(null)
}