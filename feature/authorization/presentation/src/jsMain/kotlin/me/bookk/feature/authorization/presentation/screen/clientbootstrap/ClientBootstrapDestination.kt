package me.bookk.feature.authorization.presentation.screen.clientbootstrap

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import me.bookk.core.presentation.LocalUnauthorizedHandler
import me.bookk.core.presentation.UnauthorizedHandler
import me.bookk.core.presentation.rememberViewModel

@Composable
fun ClientBootstrapDestination(
    loginContent: @Composable () -> Unit,
    mainContent: @Composable () -> Unit,
) {
    val viewModel = rememberViewModel<ClientBootstrapViewModel>()
    CompositionLocalProvider(
        LocalUnauthorizedHandler provides UnauthorizedHandler(viewModel::logOut),
    ) {
        ClientBootstrapScreen(viewModel.uiState, loginContent, mainContent)
    }
}
