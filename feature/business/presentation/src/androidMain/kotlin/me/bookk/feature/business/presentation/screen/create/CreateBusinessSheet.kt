package me.bookk.feature.business.presentation.screen.create

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import me.bookk.core.presentation.SendLifecycleEventsTo
import me.bookk.designsystem.components.ObserveNavigation
import me.bookk.designsystem.components.ObserveNotifications
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateBusinessSheet(onDismiss: () -> Unit) {
    val viewModel: CreateBusinessViewModel = koinViewModel()
    val listener = CreateBusinessEventListener(
        onNameChanged = viewModel::onBusinessNameChanged,
        onCreateClick = viewModel::onCreateClick
    )
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    val dismiss: () -> Unit = {
        scope.launch {
            sheetState.hide()
            onDismiss()
        }
    }

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = dismiss
    ) {
        CompositionLocalProvider(LocalCreateBusinessEventListener provides listener) {
            ObserveNotifications(viewModel.uiState.notifications)
            SendLifecycleEventsTo(viewModel)
            CreateBusinessScreen(viewModel.uiState)
        }

        ObserveNavigation(viewModel.uiState.navigation) {
            when (it) {
                CreateBusinessNavigationDestination.Main,
                CreateBusinessNavigationDestination.Back -> dismiss()
            }
        }
    }
}
