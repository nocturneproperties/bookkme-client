package me.bookk.feature.authorization.presentation.screen.clientbootstrap

import androidx.compose.runtime.Composable
import me.bookk.feature.authorization.presentation.bootstrap.BootstrapNavigationDestination

@Composable
internal fun ClientBootstrapScreen(
    state: ClientBootstrapState,
    loginContent: @Composable () -> Unit,
    mainContent: @Composable () -> Unit,
) {
    when (state.startDestination) {
        BootstrapNavigationDestination.Login -> loginContent()
        BootstrapNavigationDestination.Main -> mainContent()
        null -> Unit
    }
}
