package me.bookk.feature.settings.presentation.editprofile

import androidx.compose.runtime.Composable
import library.telegram.ObserveTelegramNotifications
import library.telegram.TelegramWebApp
import me.bookk.core.presentation.SendLifecycleEventsTo
import me.bookk.core.presentation.rememberViewModel
import me.bookk.designsystem.components.ObserveNavigation
import me.bookk.feature.settings.presentation.navigation.SettingsNavigation

@Composable
fun BookingProfileDestination(webApp: TelegramWebApp, navigation: SettingsNavigation) {
    val viewModel = rememberViewModel<EditProfileViewModel>()
    SendLifecycleEventsTo(viewModel)
    ObserveTelegramNotifications(webApp, viewModel.uiState.notification)
    BookingProfileScreen(viewModel.uiState, webApp)
    ObserveNavigation(viewModel.uiState.navigation) {
        when (it) {
            EditProfileNavigationDestination.Back -> navigation.onBack()
            EditProfileNavigationDestination.ChangeEmail -> navigation.toChangeEmail()
        }
    }
}
