package me.bookk.feature.business.presentation.screen.export

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import me.bookk.core.presentation.SendLifecycleEventsTo
import me.bookk.core.presentation.navigation.serializableNavTypeEntry
import me.bookk.designsystem.components.ObserveNavigation
import me.bookk.designsystem.components.ObserveNotifications
import me.bookk.feature.business.presentation.navigation.BusinessDestination
import me.bookk.feature.business.presentation.navigation.BusinessNavigation
import me.bookk.feature.business.presentation.navigation.LocalNavigation
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import kotlin.uuid.Uuid

internal fun NavGraphBuilder.businessDataExportScreen(navigation: BusinessNavigation) {
    composable<BusinessDestination.DataExport>(
        typeMap = mapOf(serializableNavTypeEntry<Uuid>()),
    ) {
        val entry = it.toRoute<BusinessDestination.DataExport>()
        val viewModel: BusinessDataExportViewModel = koinViewModel { parametersOf(entry.id) }
        val context = LocalContext.current
        CompositionLocalProvider(LocalNavigation provides navigation) {
            ObserveNotifications(viewModel.uiState.notifications)
            SendLifecycleEventsTo(viewModel)
            BusinessDataExportScreen(viewModel.uiState)
        }
        ObserveNavigation(viewModel.uiState.navigation) {
            when (it) {
                BusinessDataExportDestinations.Back -> {
                    navigation.goBack()
                }

                is BusinessDataExportDestinations.Share -> {
                    context.shareBusinessExportFile(it.filePath)
                }
            }
        }
    }
}
