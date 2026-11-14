package me.bookk.feature.settings.presentation.navigation

import androidx.navigation.NavGraphBuilder
import me.bookk.feature.settings.presentation.accdelete.deleteAccountScreen
import me.bookk.feature.settings.presentation.dashboard.dashboardScreen
import me.bookk.feature.settings.presentation.editprofile.editProfileScreen
import me.bookk.feature.settings.presentation.export.personalDataExportScreen
import me.bookk.feature.settings.presentation.notifications.notificationSettingsScreen
import me.bookk.feature.settings.presentation.passkey.passkeyScreen
import me.bookk.feature.settings.presentation.privacy.privacySettingsScreen

fun NavGraphBuilder.settingsGraph(navigation: SettingsNavigation) {
    dashboardScreen(navigation)
    editProfileScreen(navigation)
    deleteAccountScreen(navigation)
    passkeyScreen(navigation)
    notificationSettingsScreen(navigation)
    personalDataExportScreen(navigation)
    privacySettingsScreen(navigation)
}
