package me.bookk.feature.business.presentation.screen.create

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import me.bookk.core.presentation.SendLifecycleEventsTo
import me.bookk.designsystem.components.ObserveNotifications
import me.bookk.designsystem.resources.DesignSystem
import me.bookk.feature.business.presentation.navigation.BusinessDestination
import me.bookk.feature.business.presentation.navigation.BusinessNavigation
import me.bookk.feature.business.presentation.navigation.LocalNavigation
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
internal fun NavGraphBuilder.createBusinessScreen(navigation: BusinessNavigation) {
    composable<BusinessDestination.Create> {
        val viewModel: CreateBusinessViewModel = koinViewModel()
        val listener = CreateBusinessEventListener(
            onNameChanged = viewModel::onBusinessNameChanged,
            onCreateClick = viewModel::onCreateClick
        )
        var createRequested by remember { mutableStateOf(false) }

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            TextButton(onClick = { createRequested = !createRequested }) {
                Icon(Icons.Filled.Add, contentDescription = null)
                Text(DesignSystem.strings.action_create.desc().localized())
            }
        }

        if (createRequested) {
            ModalBottomSheet(
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                onDismissRequest = { createRequested = !createRequested }
            ) {
                CompositionLocalProvider(
                    LocalNavigation provides navigation,
                    LocalCreateBusinessEventListener provides listener
                ) {
                    ObserveNotifications(viewModel.uiState.notifications)
                    SendLifecycleEventsTo(viewModel)
                    CreateBusinessScreen(viewModel.uiState)
                }
            }
        }
    }
}