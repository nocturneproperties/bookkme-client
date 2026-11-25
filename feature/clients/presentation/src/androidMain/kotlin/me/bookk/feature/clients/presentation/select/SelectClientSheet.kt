package me.bookk.feature.clients.presentation.select

import androidx.activity.compose.BackHandler
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import me.bookk.designsystem.components.DesignSystemBottomSheet
import me.bookk.designsystem.components.ObserveNavigation
import me.bookk.designsystem.components.ObserveNotifications
import me.bookk.feature.clients.presentation.create.CreateClientDestination
import me.bookk.feature.clients.presentation.create.CreateClientScreen
import me.bookk.feature.clients.presentation.create.CreateClientViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import kotlin.uuid.Uuid

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectClientSheet(
    onClientSelected: (Uuid) -> Unit,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    DesignSystemBottomSheet(sheetState = sheetState, onDismiss = onDismiss) {
        val storeOwner = rememberViewModelStoreOwner()
        var newClientBusinessId by remember { mutableStateOf<Uuid?>(null) }
        val businessId = newClientBusinessId
        if (businessId == null) {
            SelectClientContent(
                storeOwner = storeOwner,
                onClientSelected = onClientSelected,
                onAddClient = { newClientBusinessId = it },
                onDismiss = onDismiss,
            )
        } else {
            NewClientContent(
                businessId = businessId,
                onCreated = onClientSelected,
                onBack = { newClientBusinessId = null },
            )
        }
    }
}

@Composable
private fun SelectClientContent(
    storeOwner: ViewModelStoreOwner,
    onClientSelected: (Uuid) -> Unit,
    onAddClient: (Uuid) -> Unit,
    onDismiss: () -> Unit,
) {
    val viewModel: SelectClientViewModel = koinViewModel(viewModelStoreOwner = storeOwner)
    ObserveNotifications(viewModel.uiState.notifications)
    SelectClientScreen(viewModel.uiState)
    ObserveNavigation(viewModel.uiState.navigation) { destination ->
        when (destination) {
            is SelectClientDestination.Selected -> onClientSelected(destination.clientId)
            is SelectClientDestination.AddClient -> onAddClient(destination.businessId)
            SelectClientDestination.Back -> onDismiss()
        }
    }
}

@Composable
private fun NewClientContent(
    businessId: Uuid,
    onCreated: (Uuid) -> Unit,
    onBack: () -> Unit,
) {
    val storeOwner = rememberViewModelStoreOwner()
    val viewModel: CreateClientViewModel = koinViewModel(viewModelStoreOwner = storeOwner) {
        parametersOf(businessId)
    }
    BackHandler(onBack = onBack)
    ObserveNotifications(viewModel.uiState.notifications)
    CreateClientScreen(viewModel.uiState)
    ObserveNavigation(viewModel.uiState.navigation) { destination ->
        when (destination) {
            is CreateClientDestination.Details -> onCreated(destination.id)
            CreateClientDestination.Back -> onBack()
        }
    }
}

@Composable
private fun rememberViewModelStoreOwner(): ViewModelStoreOwner {
    val storeOwner = remember {
        object : ViewModelStoreOwner {
            override val viewModelStore = ViewModelStore()
        }
    }
    DisposableEffect(storeOwner) {
        onDispose { storeOwner.viewModelStore.clear() }
    }
    return storeOwner
}
