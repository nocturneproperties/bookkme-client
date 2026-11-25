package me.bookk.feature.settings.presentation.navigation

class SettingsNavigation(
    val onBack: () -> Unit,
    val toBookingProfile: () -> Unit,
    val toNotifications: () -> Unit,
    val toPrivacy: () -> Unit,
    val toPersonalDataExport: () -> Unit,
    val toDeleteAccount: () -> Unit,
    val openContactPage: () -> Unit,
    val openTermsOfUse: () -> Unit,
    val openPrivacyPolicy: () -> Unit,
)
