package me.bookk.designsystem.components

import androidx.compose.runtime.Composable
import me.bookk.core.presentation.navigation.NavigationDestination
import me.bookk.designsystem.uistate.NavigationState

@Composable
fun <T : NavigationDestination> ObserveNavigation(state: NavigationState<T>, action: @Composable (T) -> Unit) {
    state.navigationDestination.forEach { destination ->
        action(destination)
        state.removeFirst()
    }
}