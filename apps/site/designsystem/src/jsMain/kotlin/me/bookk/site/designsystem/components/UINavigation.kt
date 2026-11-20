package me.bookk.site.designsystem.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import me.bookk.core.presentation.navigation.NavigationDestination
import me.bookk.designsystem.uistate.NavigationState

@Composable
fun <T : NavigationDestination> ObserveNavigation(
    state: NavigationState<T>,
    onNavigate: (T) -> Unit,
) {
    val destination = state.navigationDestination.firstOrNull() ?: return
    LaunchedEffect(destination) {
        state.removeFirst()
        onNavigate(destination)
    }
}
