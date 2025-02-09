package me.bookk.feature.settings.presentation.navigation

import me.bookk.core.presentation.navigation.NavigationDestinationDeclaration

object SettingsDashboardDestination : NavigationDestinationDeclaration() {
    override val route: String = "settings.dashboard"
}

object EditProfileDestination : NavigationDestinationDeclaration() {
    override val route: String = "settings.edit_profile"
}

object PasskeyDestination : NavigationDestinationDeclaration() {
    override val route: String = "settings.passkey"
}

object DeleteAccountDestination : NavigationDestinationDeclaration() {
    override val route: String = "settings.delete_acc"
}

object ContactDestination : NavigationDestinationDeclaration() {
    override val route: String = "settings.contact_us"
}

object SuggestFeatureDestination : NavigationDestinationDeclaration() {
    override val route: String = "settings.feature"
}

object ReportDestination : NavigationDestinationDeclaration() {
    override val route: String = "settings.report"
}
