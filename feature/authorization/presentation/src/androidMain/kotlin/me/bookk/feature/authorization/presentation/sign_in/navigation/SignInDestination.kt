package me.bookk.feature.authorization.presentation.sign_in.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import me.bookk.designsystem.components.ObserveNotifications
import me.bookk.feature.authorization.presentation.navigation.AuthDestination
import me.bookk.feature.authorization.presentation.navigation.AuthNavigation
import me.bookk.feature.authorization.presentation.navigation.LocalNavigation
import me.bookk.feature.authorization.presentation.sign_in.SignInNavigationDestination
import me.bookk.feature.authorization.presentation.sign_in.SignInScreen
import me.bookk.feature.authorization.presentation.sign_in.SignInViewModel
import org.koin.androidx.compose.koinViewModel

internal fun NavGraphBuilder.signInScreen(navigation: AuthNavigation) {
    composable<AuthDestination.SignIn> {
        val viewModel: SignInViewModel = koinViewModel()

        ObserveNotifications(state = viewModel.uiState.notification)

        CompositionLocalProvider(LocalNavigation provides navigation) {
            HandleNavigation(viewModel = viewModel)

            SignInScreen(
                state = viewModel.uiState,
                listener = viewModel
            )
        }
    }
}

@Composable
private fun HandleNavigation(viewModel: SignInViewModel) {
    viewModel.uiState.navigation.navigationDestination?.let {
        when (it) {
            SignInNavigationDestination.ToMain -> LocalNavigation.current.navigateToMainScreen()
        }
        viewModel.uiState.navigation.navigationDestination = null
    }
}