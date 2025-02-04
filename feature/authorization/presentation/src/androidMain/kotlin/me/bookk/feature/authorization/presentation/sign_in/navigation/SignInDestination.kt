package me.bookk.feature.authorization.presentation.sign_in.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import me.bookk.designsystem.components.ObserveErrors
import me.bookk.feature.authorization.presentation.navigation.SignInDestination
import me.bookk.feature.authorization.presentation.sign_in.SignInNavigationDestination
import me.bookk.feature.authorization.presentation.sign_in.SignInScreen
import me.bookk.feature.authorization.presentation.sign_in.SignInViewModel
import org.koin.androidx.compose.koinViewModel

internal fun NavGraphBuilder.signInScreen(
    navigateBack: () -> Unit,
    navigateToMainScreen: () -> Unit,
    navigateToSignUp: () -> Unit,
    navigateToTroubleshoot: () -> Unit
) {
    composable(route = SignInDestination.route) {
        val viewModel: SignInViewModel = koinViewModel()

        ObserveErrors(state = viewModel.uiState.error)
        HandleNavigation(
            viewModel = viewModel,
            navigateBack = navigateBack,
            navigateToMainScreen = navigateToMainScreen,
            navigateToSignUp = navigateToSignUp,
            navigateToTroubleshoot = navigateToTroubleshoot
        )

        SignInScreen(
            state = viewModel.uiState,
            listener = viewModel,
        )
    }
}

@Composable
private fun HandleNavigation(
    viewModel: SignInViewModel,
    navigateBack: () -> Unit,
    navigateToMainScreen: () -> Unit,
    navigateToSignUp: () -> Unit,
    navigateToTroubleshoot: () -> Unit
) {
    viewModel.uiState.navigation.navigationDestination?.let {
        when (it) {
            SignInNavigationDestination.Back -> navigateBack()
            SignInNavigationDestination.ToMain -> navigateToMainScreen()
            SignInNavigationDestination.ToSignUp -> navigateToSignUp()
            SignInNavigationDestination.ToTroubleshoot -> navigateToTroubleshoot()
        }
        viewModel.uiState.navigation.navigationDestination = null
    }
}