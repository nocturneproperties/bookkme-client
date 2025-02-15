package me.bookk.feature.authorization.presentation.navigation

import androidx.navigation.NavGraphBuilder
import me.bookk.feature.authorization.presentation.sign_in.navigation.signInScreen
import me.bookk.feature.authorization.presentation.sign_up.navigation.signUpScreen
import me.bookk.feature.authorization.presentation.troubleshoot.navigation.troubleshootScreen

fun NavGraphBuilder.authGraph(navigation: AuthNavigation) {
    signUpScreen(navigation)
    signInScreen(navigation)
    troubleshootScreen(navigation)
}