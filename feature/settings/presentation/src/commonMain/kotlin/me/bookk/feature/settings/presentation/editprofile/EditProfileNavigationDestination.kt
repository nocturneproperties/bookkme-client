package me.bookk.feature.settings.presentation.editprofile

import me.bookk.core.presentation.navigation.NavigationDestination

sealed class EditProfileNavigationDestination : NavigationDestination() {
    data object Back : EditProfileNavigationDestination()
}