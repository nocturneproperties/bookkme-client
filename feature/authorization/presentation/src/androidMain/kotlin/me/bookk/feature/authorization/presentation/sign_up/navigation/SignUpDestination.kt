package me.bookk.feature.authorization.presentation.sign_up.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import me.bookk.designsystem.components.ObserveErrors
import me.bookk.feature.authorization.presentation.navigation.SignUpDestination
import me.bookk.feature.authorization.presentation.sign_up.SignUpNavigationEvent
import me.bookk.feature.authorization.presentation.sign_up.SignUpScreen
import me.bookk.feature.authorization.presentation.sign_up.SignUpViewModel
import org.koin.androidx.compose.koinViewModel

internal fun NavGraphBuilder.signUpScreen(
    navigateToMainScreen: () -> Unit
) {
    composable(route = SignUpDestination.route) {
        val viewModel: SignUpViewModel = koinViewModel()

        ObserveErrors(errorFlow = viewModel.errorFlow)

        LaunchedEffect(viewModel.navigationFlow) {
            viewModel.navigationFlow.collect { event ->
                when (event) {
                    SignUpNavigationEvent.ToMain -> navigateToMainScreen()
                }
            }
        }

        SignUpScreen(
            state = viewModel.uiState,
            listener = viewModel,
        )
    }
}