package me.bookk.feature.authorization.presentation.sign_up.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import me.bookk.designsystem.components.ObserveErrors
import me.bookk.feature.authorization.presentation.navigation.SignUpDestination
import me.bookk.feature.authorization.presentation.sign_up.SignUpNavigationDestination
import me.bookk.feature.authorization.presentation.sign_up.SignUpScreen
import me.bookk.feature.authorization.presentation.sign_up.SignUpViewModel
import org.koin.androidx.compose.koinViewModel

internal fun NavGraphBuilder.signUpScreen(
    navigateBack: () -> Unit,
    navigateToMainScreen: () -> Unit
) {
    composable(route = SignUpDestination.route) {
        val viewModel: SignUpViewModel = koinViewModel()

        ObserveErrors(state = viewModel.uiState.error)

        HandleNavigation(
            viewModel = viewModel,
            navigateBack = navigateBack,
            navigateToMainScreen = navigateToMainScreen,
        )

        SignUpScreen(
            state = viewModel.uiState,
            listener = viewModel,
        )
    }
}

@Composable
private fun HandleNavigation(
    viewModel: SignUpViewModel,
    navigateBack: () -> Unit,
    navigateToMainScreen: () -> Unit
) {
    viewModel.uiState.navigation.navigationDestination?.let {
        when (it) {
            SignUpNavigationDestination.Back -> navigateBack()
            SignUpNavigationDestination.ToMain -> navigateToMainScreen()
        }
        viewModel.uiState.navigation.navigationDestination = null
    }
}