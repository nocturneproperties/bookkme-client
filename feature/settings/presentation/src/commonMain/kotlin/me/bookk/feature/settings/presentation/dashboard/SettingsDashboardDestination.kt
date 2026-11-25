package me.bookk.feature.settings.presentation.dashboard

import me.bookk.core.presentation.navigation.NavigationDestination

sealed class SettingsDashboardDestination : NavigationDestination() {
    data object EditProfile : SettingsDashboardDestination()
    data object Notifications : SettingsDashboardDestination()
    data object Privacy : SettingsDashboardDestination()
    data object PersonalDataExport : SettingsDashboardDestination()
    data object DeleteAccount : SettingsDashboardDestination()
    data object ContactPage : SettingsDashboardDestination()
    data object TermsOfUse : SettingsDashboardDestination()
    data object PrivacyPolicy : SettingsDashboardDestination()
}
