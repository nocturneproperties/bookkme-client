package me.bookk.feature.authorization.presentation.navigation

import androidx.navigation.NavGraphBuilder
import me.bookk.feature.authorization.presentation.sign_up.navigation.signUpScreen

fun NavGraphBuilder.authGraph() {
    signUpScreen()
}