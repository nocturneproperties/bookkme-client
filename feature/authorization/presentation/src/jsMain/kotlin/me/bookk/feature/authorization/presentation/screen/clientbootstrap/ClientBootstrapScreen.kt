package me.bookk.feature.authorization.presentation.screen.clientbootstrap

import androidx.compose.runtime.Composable
import me.bookk.core.presentation.rememberViewModel
import me.bookk.feature.authorization.presentation.bootstrap.BootstrapNavigationDestination

@Composable
fun ClientBootstrapScreen(
    loginContent: @Composable () -> Unit,
    mainContent: @Composable () -> Unit,
) {
    val state = rememberViewModel<ClientBootstrapViewModel>().uiState
    when (state.startDestination) {
        BootstrapNavigationDestination.Login -> loginContent()
        BootstrapNavigationDestination.Main -> mainContent()
        null -> Unit
    }
}
