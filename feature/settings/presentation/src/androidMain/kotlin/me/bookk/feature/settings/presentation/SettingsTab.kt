package me.bookk.feature.settings.presentation

import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import me.bookk.feature.settings.presentation.navigation.SettingsDestination
import me.bookk.feature.settings.presentation.navigation.SettingsNavigation
import me.bookk.feature.settings.presentation.navigation.settingsGraph

@Composable
fun SettingsTab() {
    val settingsController = rememberNavController()
    NavHost(
        navController = settingsController,
        startDestination = SettingsDestination.Dashboard,
        enterTransition = { slideIntoContainer(SlideDirection.Start, tween(400)) },
        exitTransition = { scaleOut(targetScale = 0.92f) },
        popEnterTransition = { scaleIn(initialScale = 0.92f) },
        popExitTransition = { slideOutOfContainer(SlideDirection.End, tween(400)) }
    ) {
        settingsGraph(
            navigation = SettingsNavigation(
                navigateBack = { settingsController.popBackStack() },
                navigateToEditProfile = {
                    settingsController.navigate(SettingsDestination.EditProfile)
                },
                navigateToPasskey = {
                    settingsController.navigate(SettingsDestination.Passkey)
                },
                navigateToContact = {
                    settingsController.navigate(SettingsDestination.ContactUs)
                },
                navigateToDeleteAccount = {
                    settingsController.navigate(SettingsDestination.DeleteAccount)
                },
                navigateToNotifications = {
                    settingsController.navigate(SettingsDestination.Notifications)
                }
            )
        )
    }
}