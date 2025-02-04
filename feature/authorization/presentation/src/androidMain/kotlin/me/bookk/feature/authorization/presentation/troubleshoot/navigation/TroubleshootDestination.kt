package me.bookk.feature.authorization.presentation.troubleshoot.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import me.bookk.feature.authorization.presentation.navigation.TroubleshootDestination
import me.bookk.feature.authorization.presentation.troubleshoot.TroubleshootNavigationDestination
import me.bookk.feature.authorization.presentation.troubleshoot.TroubleshootScreen
import me.bookk.feature.authorization.presentation.troubleshoot.TroubleshootViewModel
import org.koin.androidx.compose.koinViewModel

internal fun NavGraphBuilder.troubleshootScreen(
    navigateBack: () -> Unit,
    navigateToContactSupport: () -> Unit
) {
    composable(route = TroubleshootDestination.route) {
        val viewModel: TroubleshootViewModel = koinViewModel()

        HandleNavigation(
            viewModel = viewModel,
            navigateBack = navigateBack,
            navigateToContactSupport = navigateToContactSupport,
        )

        TroubleshootScreen(
            state = viewModel.uiState,
            listener = viewModel,
        )
    }
}

@Composable
private fun HandleNavigation(
    viewModel: TroubleshootViewModel,
    navigateBack: () -> Unit,
    navigateToContactSupport: () -> Unit
) {
    viewModel.uiState.navigation.navigationDestination?.let {
        when (it) {
            TroubleshootNavigationDestination.Back -> navigateBack()
            TroubleshootNavigationDestination.ContactSupport -> navigateToContactSupport()
        }
        viewModel.uiState.navigation.navigationDestination = null
    }
}