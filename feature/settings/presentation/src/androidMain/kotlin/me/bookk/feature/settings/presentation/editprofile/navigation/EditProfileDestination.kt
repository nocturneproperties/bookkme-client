package me.bookk.feature.settings.presentation.editprofile.navigation

import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import me.bookk.feature.settings.presentation.editprofile.EditProfileScreen
import me.bookk.feature.settings.presentation.navigation.LocalNavigation
import me.bookk.feature.settings.presentation.navigation.SettingsDestination
import me.bookk.feature.settings.presentation.navigation.SettingsNavigation

internal fun NavGraphBuilder.editProfileScreen(navigation: SettingsNavigation) {
    composable<SettingsDestination.EditProfile> {

        CompositionLocalProvider(
            LocalNavigation provides navigation
        ) {
            EditProfileScreen()
        }
    }
}