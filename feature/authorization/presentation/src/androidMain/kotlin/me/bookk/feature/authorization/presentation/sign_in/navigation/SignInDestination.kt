package me.bookk.feature.authorization.presentation.sign_in.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import me.bookk.designsystem.components.ObserveErrors
import me.bookk.feature.authorization.presentation.navigation.SignInDestination
import me.bookk.feature.authorization.presentation.sign_in.SignInNavigationEvent
import me.bookk.feature.authorization.presentation.sign_in.SignInScreen
import me.bookk.feature.authorization.presentation.sign_in.SignInViewModel
import org.koin.androidx.compose.koinViewModel

internal fun NavGraphBuilder.signInScreen(navigateToMainScreen: () -> Unit) {
    composable(route = SignInDestination.route) {
        val viewModel: SignInViewModel = koinViewModel()

        ObserveErrors(errorFlow = viewModel.errorFlow)

        LaunchedEffect(viewModel.navigationFlow) {
            viewModel.navigationFlow.collect { event ->
                when (event) {
                    SignInNavigationEvent.ToMain -> navigateToMainScreen()
                }
            }
        }

        SignInScreen(
            state = viewModel.uiState,
            listener = viewModel,
        )
    }
}