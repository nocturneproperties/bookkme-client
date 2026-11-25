package me.bookk.feature.settings.presentation.dashboard

import androidx.compose.runtime.Composable
import library.telegram.ObserveTelegramNotifications
import library.telegram.TelegramBackButton
import library.telegram.TelegramWebApp
import me.bookk.core.presentation.SendLifecycleEventsTo
import me.bookk.core.presentation.rememberViewModel
import me.bookk.designsystem.components.ObserveNavigation
import me.bookk.feature.settings.presentation.navigation.SettingsNavigation

@Composable
fun SettingsDestination(webApp: TelegramWebApp, navigation: SettingsNavigation) {
    val viewModel = rememberViewModel<SettingsDashboardViewModel>()
    SendLifecycleEventsTo(viewModel)
    ObserveTelegramNotifications(webApp, viewModel.uiState.notification)
    TelegramBackButton(webApp, navigation.onBack)
    SettingsScreen(viewModel.uiState)
    ObserveNavigation(viewModel.uiState.navigation) {
        when (it) {
            SettingsDashboardDestination.EditProfile -> {
                navigation.toBookingProfile()
            }

            SettingsDashboardDestination.Notifications -> {
                navigation.toNotifications()
            }

            SettingsDashboardDestination.Privacy -> {
                navigation.toPrivacy()
            }

            SettingsDashboardDestination.PersonalDataExport -> {
                navigation.toPersonalDataExport()
            }

            SettingsDashboardDestination.DeleteAccount -> {
                navigation.toDeleteAccount()
            }

            SettingsDashboardDestination.ContactPage -> {
                navigation.openContactPage()
            }

            SettingsDashboardDestination.TermsOfUse -> {
                navigation.openTermsOfUse()
            }

            SettingsDashboardDestination.PrivacyPolicy -> {
                navigation.openPrivacyPolicy()
            }
        }
    }
}
