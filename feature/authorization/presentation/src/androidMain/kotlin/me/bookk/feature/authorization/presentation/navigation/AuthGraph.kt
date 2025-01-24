package me.bookk.feature.authorization.presentation.navigation

import androidx.navigation.NavGraphBuilder
import me.bookk.feature.authorization.presentation.sign_in.navigation.signInScreen
import me.bookk.feature.authorization.presentation.sign_up.navigation.signUpScreen

fun NavGraphBuilder.authGraph(
    navigateBack: () -> Unit,
    navigateToMainScreen: () -> Unit,
    navigateToSignUp: () -> Unit
) {
    signUpScreen(navigateBack, navigateToMainScreen)
    signInScreen(navigateBack, navigateToMainScreen, navigateToSignUp)
}