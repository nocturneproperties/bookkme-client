package me.bookk.feature.settings.presentation.export

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import me.bookk.core.presentation.SendLifecycleEventsTo
import me.bookk.designsystem.components.ObserveNavigation
import me.bookk.designsystem.components.ObserveNotifications
import me.bookk.feature.settings.presentation.navigation.LocalNavigation
import me.bookk.feature.settings.presentation.navigation.SettingsDestination
import me.bookk.feature.settings.presentation.navigation.SettingsNavigation
import org.koin.androidx.compose.koinViewModel

internal fun NavGraphBuilder.personalDataExportScreen(navigation: SettingsNavigation) {
    composable<SettingsDestination.PersonalDataExport> {
        val viewModel: PersonalDataExportViewModel = koinViewModel()
        val context = LocalContext.current
        CompositionLocalProvider(LocalNavigation provides navigation) {
            ObserveNotifications(viewModel.uiState.notifications)
            SendLifecycleEventsTo(viewModel)
            PersonalDataExportScreen(viewModel.uiState)
        }
        ObserveNavigation(viewModel.uiState.navigation) {
            when (it) {
                PersonalDataExportDestinations.Back -> navigation.navigateBack()
                is PersonalDataExportDestinations.Share -> context.shareExportFile(it.filePath)
            }
        }
    }
}
