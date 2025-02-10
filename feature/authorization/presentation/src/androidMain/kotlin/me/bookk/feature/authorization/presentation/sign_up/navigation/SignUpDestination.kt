package me.bookk.feature.authorization.presentation.sign_up.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import me.bookk.designsystem.components.ObserveErrors
import me.bookk.feature.authorization.presentation.navigation.AuthNavigation
import me.bookk.feature.authorization.presentation.navigation.LocalNavigation
import me.bookk.feature.authorization.presentation.navigation.SignUpDestination
import me.bookk.feature.authorization.presentation.sign_up.SignUpNavigationDestination
import me.bookk.feature.authorization.presentation.sign_up.SignUpScreen
import me.bookk.feature.authorization.presentation.sign_up.SignUpViewModel
import org.koin.androidx.compose.koinViewModel

internal fun NavGraphBuilder.signUpScreen(navigation: AuthNavigation) {
    composable(route = SignUpDestination.route) {
        val viewModel: SignUpViewModel = koinViewModel()

        ObserveErrors(state = viewModel.uiState.error)

        CompositionLocalProvider(LocalNavigation provides navigation) {
            HandleNavigation(viewModel = viewModel)

            SignUpScreen(
                state = viewModel.uiState,
                listener = viewModel,
            )
        }
    }
}

@Composable
private fun HandleNavigation(viewModel: SignUpViewModel) {
    viewModel.uiState.navigation.navigationDestination?.let {
        when (it) {
            SignUpNavigationDestination.ToMain -> LocalNavigation.current.navigateToMainScreen()
        }
        viewModel.uiState.navigation.navigationDestination = null
    }
}