package me.bookk.feature.authorization.presentation.sign_up.navigation

import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import me.bookk.designsystem.components.ObserveNavigation
import me.bookk.feature.authorization.presentation.navigation.AuthDestination
import me.bookk.feature.authorization.presentation.navigation.AuthNavigation
import me.bookk.feature.authorization.presentation.navigation.LocalNavigation
import me.bookk.feature.authorization.presentation.sign_up.SignUpNavigationDestination
import me.bookk.feature.authorization.presentation.sign_up.SignUpScreen
import me.bookk.feature.authorization.presentation.sign_up.SignUpViewModel
import org.koin.androidx.compose.koinViewModel

internal fun NavGraphBuilder.signUpScreen(navigation: AuthNavigation) {
    composable<AuthDestination.SignUp> {
        val viewModel: SignUpViewModel = koinViewModel()

        CompositionLocalProvider(LocalNavigation provides navigation) {
            SignUpScreen(
                state = viewModel.uiState,
                listener = viewModel,
            )

            ObserveNavigation(viewModel.uiState.navigation) {
                when (it) {
                    SignUpNavigationDestination.Back -> navigation.navigateBack()
                }
            }
        }
    }
}