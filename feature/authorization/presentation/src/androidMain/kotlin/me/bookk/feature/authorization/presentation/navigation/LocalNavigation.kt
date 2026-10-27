package me.bookk.feature.authorization.presentation.navigation

import androidx.compose.runtime.compositionLocalOf

class AuthNavigation(
    val navigateBack: () -> Unit,
    val navigateToMainScreen: () -> Unit,
    val navigateToSignUp: () -> Unit,
    val navigateToContactSupport: () -> Unit,
    val navigateToTroubleshoot: () -> Unit
)

internal val LocalNavigation = compositionLocalOf {
    AuthNavigation(
        navigateBack = {},
        navigateToMainScreen = {},
        navigateToSignUp = {},
        navigateToContactSupport = {},
        navigateToTroubleshoot = {}
    )
}