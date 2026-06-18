package library.picker

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import me.bookk.core.domain.entity.KeyValueData
import me.bookk.designsystem.components.DesignSystemBottomSheet
import me.bookk.designsystem.components.ObserveNavigation
import me.bookk.designsystem.uistate.PickerFieldState
import me.bookk.designsystem.uistate.PickerPresentation
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PickOptionFullScreenDialog(
    args: PickerScreenArgs,
    onDismiss: () -> Unit,
    onResultSelected: (KeyValueData) -> Unit
) {
    val state = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    DesignSystemBottomSheet(
        sheetState = state,
        onDismiss = onDismiss
    ) {
        val customStoreOwner = remember {
            object : ViewModelStoreOwner {
                override val viewModelStore = ViewModelStore()
            }
        }
        val viewModel: PickOptionViewModel = koinViewModel(viewModelStoreOwner = customStoreOwner) {
            parametersOf(args)
        }
        PickOptionScreen(viewModel.uiState)
        ObserveNavigation(state = viewModel.uiState.navigation) { destination ->
            when (destination) {
                is PickerNavigationDestination.FinishWithResult -> {
                    onResultSelected(destination.pickResult)
                }

                PickerNavigationDestination.Back -> {
                    onDismiss()
                }
            }
        }
    }
}


fun <T : PickerPresentation> standardScreenPicker() = @Composable { state: PickerFieldState<T>,
                                                                    onDismiss: () -> Unit,
                                                                    onItemPicked: (T) -> Unit ->
    val context = LocalContext.current
    val args = remember(state.options) {
        PickerScreenArgs(
            id = state.id,
            title = state.pickerTitle.toString(context),
            options = state.options.map { presentation ->
                PickerScreenArgs.PickerData(
                    iconUrl = presentation.displayIconUrl,
                    data = KeyValueData(
                        key = presentation.pickerItemId,
                        value = presentation.displayName.toString(context)
                    )
                )
            },
            choice = PickerScreenArgs.Choice.SINGLE
        )
    }
    PickOptionFullScreenDialog(args, onDismiss) { pickedItem ->
        state.options
            .firstOrNull { it.pickerItemId == pickedItem.key }
            ?.let { onItemPicked(it) }
    }
}