package me.bookk.feature.settings.presentation.navigation

import androidx.navigation.NavGraphBuilder
import me.bookk.feature.settings.presentation.contactus.contactUsScreen
import me.bookk.feature.settings.presentation.dashboard.dashboardScreen
import me.bookk.feature.settings.presentation.editprofile.editProfileScreen

fun NavGraphBuilder.settingsGraph(navigation: SettingsNavigation) {
    dashboardScreen(navigation)
    editProfileScreen(navigation)
    contactUsScreen(navigation)
}