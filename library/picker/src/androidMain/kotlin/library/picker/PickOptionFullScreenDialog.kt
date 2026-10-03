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
import dev.icerock.moko.resources.desc.StringDesc
import me.bookk.designsystem.uistate.OptionsMultiPickerState
import me.bookk.designsystem.uistate.PickerFieldState
import me.bookk.designsystem.uistate.PickerPresentation
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PickOptionFullScreenDialog(
    args: PickerScreenArgs,
    onDismiss: () -> Unit,
    onResultSelected: (List<KeyValueData>) -> Unit
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
                    onResultSelected(destination.pickResults)
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
    PresentationPickOptionDialog(
        id = state.id,
        title = state.pickerTitle,
        options = state.options,
        choice = PickerScreenArgs.Choice.SINGLE,
        onDismiss = onDismiss,
        onItemsPicked = { it.firstOrNull()?.let(onItemPicked) }
    )
}

fun <T : PickerPresentation> standardOptionsScreenPicker() = @Composable { state: OptionsMultiPickerState<T>,
                                                                           onDismiss: () -> Unit,
                                                                           onItemsPicked: (List<T>) -> Unit ->
    PresentationPickOptionDialog(
        id = state.id,
        title = state.pickerTitle,
        options = state.options,
        choice = PickerScreenArgs.Choice.MULTIPLE,
        onDismiss = onDismiss,
        onItemsPicked = onItemsPicked
    )
}

@Composable
private fun <T : PickerPresentation> PresentationPickOptionDialog(
    id: String,
    title: StringDesc,
    options: List<T>,
    choice: PickerScreenArgs.Choice,
    onDismiss: () -> Unit,
    onItemsPicked: (List<T>) -> Unit
) {
    val context = LocalContext.current
    val args = remember(options) {
        PickerScreenArgs(
            id = id,
            title = title.toString(context),
            options = options.map { presentation ->
                PickerScreenArgs.PickerData(
                    iconUrl = presentation.displayIconUrl,
                    data = KeyValueData(
                        key = presentation.pickerItemId,
                        value = presentation.displayName.toString(context)
                    )
                )
            },
            choice = choice
        )
    }
    PickOptionFullScreenDialog(args, onDismiss) { pickedItems ->
        val pickedKeys = pickedItems.map { it.key }.toSet()
        onItemsPicked(options.filter { it.pickerItemId in pickedKeys })
    }
}