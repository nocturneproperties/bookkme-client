package me.bookk.feature.services.presentation.group.add

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import dev.icerock.moko.resources.compose.localized
import me.bookk.designsystem.components.ActionButton
import me.bookk.designsystem.components.DesignSystemBottomSheet
import me.bookk.designsystem.components.ObserveNavigation
import me.bookk.designsystem.components.ObserveNotifications
import me.bookk.designsystem.components.TextField
import org.koin.androidx.compose.koinViewModel

@Composable
internal fun AddGroupDialog(onDismiss: () -> Unit) {
    val customStoreOwner = remember {
        object : ViewModelStoreOwner {
            override val viewModelStore = ViewModelStore()
        }
    }
    val viewModel: AddGroupViewModel = koinViewModel(viewModelStoreOwner = customStoreOwner)
    val state = viewModel.uiState
    ObserveNotifications(state.notifications)
    ObserveNavigation(state.navigation) {
        when (it) {
            AddGroupNavigation.Dismiss -> onDismiss()
        }
    }
    DesignSystemBottomSheet(
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        title = state.title.localized(),
        onDismiss = onDismiss
    ) {
        TextField(state.name)
        ActionButton(state.create, Modifier.fillMaxWidth())
    }
}