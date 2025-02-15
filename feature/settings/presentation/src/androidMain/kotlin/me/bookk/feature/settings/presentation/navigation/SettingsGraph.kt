package me.bookk.feature.settings.presentation.navigation

import androidx.navigation.NavGraphBuilder
import me.bookk.feature.settings.presentation.dashboard.navigation.dashboardScreen
import me.bookk.feature.settings.presentation.editprofile.navigation.editProfileScreen

fun NavGraphBuilder.settingsGraph(navigation: SettingsNavigation) {
    dashboardScreen(navigation)
    editProfileScreen(navigation)
}