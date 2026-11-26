package me.bookk.feature.settings.presentation.confirmaction

import androidx.compose.runtime.Composable
import library.telegram.ObserveTelegramNotifications
import library.telegram.TelegramWebApp
import me.bookk.core.domain.launch.ConfirmableAction
import me.bookk.core.presentation.SendLifecycleEventsTo
import me.bookk.core.presentation.rememberViewModel
import me.bookk.designsystem.components.ObserveNavigation
import me.bookk.feature.settings.presentation.navigation.SettingsNavigation
import org.koin.core.parameter.parametersOf

@Composable
fun ConfirmActionDestination(
    webApp: TelegramWebApp,
    action: ConfirmableAction,
    navigation: SettingsNavigation,
) {
    val viewModel = rememberViewModel<ConfirmActionViewModel>(action) {
        parametersOf(action, webApp.initData)
    }
    SendLifecycleEventsTo(viewModel)
    ObserveTelegramNotifications(webApp, viewModel.uiState.notifications)
    ConfirmActionScreen(viewModel.uiState, webApp)
    ObserveNavigation(viewModel.uiState.navigation) {
        when (it) {
            ConfirmActionDestinations.Back -> {
                navigation.onBack()
            }

            ConfirmActionDestinations.Settings -> {
                navigation.returnToSettings()
            }

            ConfirmActionDestinations.PersonalDataExport -> {
                navigation.showStartedExport()
            }
        }
    }
}
