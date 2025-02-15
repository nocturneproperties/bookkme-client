package me.bookk.feature.authorization.presentation.troubleshoot.navigation

import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import me.bookk.feature.authorization.presentation.navigation.AuthDestination
import me.bookk.feature.authorization.presentation.navigation.AuthNavigation
import me.bookk.feature.authorization.presentation.navigation.LocalNavigation
import me.bookk.feature.authorization.presentation.troubleshoot.TroubleshootScreen
import me.bookk.feature.authorization.presentation.troubleshoot.TroubleshootViewModel
import org.koin.androidx.compose.koinViewModel

internal fun NavGraphBuilder.troubleshootScreen(navigation: AuthNavigation) {
    composable<AuthDestination.Troubleshoot> {
        val viewModel: TroubleshootViewModel = koinViewModel()

        CompositionLocalProvider(LocalNavigation provides navigation) {
            TroubleshootScreen(
                state = viewModel.uiState,
                listener = viewModel,
            )
        }
    }
}