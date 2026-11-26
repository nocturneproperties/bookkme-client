package me.bookk.feature.settings.presentation.export

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import library.files.api.FileProvider
import library.telegram.ObserveTelegramNotifications
import library.telegram.TelegramWebApp
import me.bookk.core.domain.launch.ConfirmableAction
import me.bookk.core.presentation.SendLifecycleEventsTo
import me.bookk.core.presentation.rememberViewModel
import me.bookk.designsystem.components.ObserveNavigation
import me.bookk.feature.settings.presentation.confirmaction.ConfirmTelegramAction
import me.bookk.feature.settings.presentation.navigation.SettingsNavigation
import org.koin.mp.KoinPlatform

@Composable
fun PersonalDataExportDestination(
    webApp: TelegramWebApp,
    navigation: SettingsNavigation,
    confirmsInTelegram: Boolean = false,
) {
    val viewModel = rememberViewModel<PersonalDataExportViewModel>()
    val fileProvider = remember { KoinPlatform.getKoin().get<FileProvider>() }
    if (confirmsInTelegram) {
        ConfirmTelegramAction(webApp, ConfirmableAction.EXPORT_PERSONAL_DATA)
    }
    SendLifecycleEventsTo(viewModel)
    ObserveTelegramNotifications(webApp, viewModel.uiState.notifications)
    PersonalDataExportScreen(viewModel.uiState, webApp)
    ObserveNavigation(viewModel.uiState.navigation) {
        when (it) {
            PersonalDataExportDestinations.Back -> {
                navigation.onBack()
            }

            is PersonalDataExportDestinations.Share -> {
                shareExportFile(fileProvider, it.filePath)
            }
        }
    }
}
