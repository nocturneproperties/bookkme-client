package library.picker

import dev.icerock.moko.resources.desc.desc
import dev.icerock.moko.resources.desc.image.asImageUrl
import library.picker.PickerNavigationDestination.FinishWithResult
import library.picker.PickerScreenArgs.Choice
import library.picker.state.PickOptionItem
import library.picker.state.PickOptionState
import me.bookk.core.presentation.ViewModel
import me.bookk.core.presentation.VmArgs
import me.bookk.core.presentation.memory.weakSelfClosure
import me.bookk.designsystem.resources.DesignSystem
import me.bookk.designsystem.uistate.CheckBoxState
import me.bookk.designsystem.uistate.simple.EmptyState

class PickOptionViewModel(
    private val pickArgs: PickerScreenArgs,
    private val factory: PickOptionStateFactory,
    vmArgs: VmArgs
) : ViewModel(vmArgs) {

    private var options = listOf<PickOptionItem>()

    val uiState = factory.createPickOptionState().setup()

    private fun PickOptionState.setup() = apply {
        appBar.title = pickArgs.title.desc()

        val items = pickArgs.options.map {
            factory.createPickOptionItem().apply {
                id = it.data.key
                identity = it.data
                icon = it.iconUrl?.asImageUrl()
                checkBox.text = it.data.value.desc()
                checkBox.isChecked = false
                checkBox.onCheckedChange = weakSelfClosure { vm, _ -> vm.onItemSelected(checkBox) }
            }
        }
        options = items
        filteredOptions.replace(items)
        filteredOptions.isInitialLoading = false
        filteredOptions.emptyState = EmptyState(
            image = DesignSystem.images.empty,
            label = DesignSystem.strings.list_empty.desc()
        )

        queryField.placeholder = DesignSystem.strings.action_search.desc()
        queryField.onTextChanged = weakSelfClosure { vm, text ->  vm.onFilterChanged(text) }

        selectButton.isVisible = pickArgs.choice == Choice.MULTIPLE
        selectButton.text = DesignSystem.strings.action_select.desc()
        selectButton.onClick = weakSelfClosure { it.onItemsPicked() }
        selectButton.isEnabled = false
    }

    private fun onItemsPicked() {
        val pickedItem = options.firstOrNull { it.checkBox.isChecked } ?: return
        uiState.navigation.push(FinishWithResult(pickArgs.id, pickedItem.identity))
    }

    private fun onFilterChanged(newFilter: String) {
        uiState.queryField.text = newFilter
        val filtered = options.filter {
            it.identity.value.contains(newFilter, ignoreCase = true)
        }
        uiState.filteredOptions.replace(filtered)
        uiState.selectButton.isEnabled = filtered.isNotEmpty() &&
                filtered.any { it.checkBox.isChecked }
    }

    private fun onItemSelected(item: CheckBoxState) {
        options.forEach { item ->
            item.checkBox.isChecked = item.checkBox.isChecked && pickArgs.choice == Choice.MULTIPLE
        }
        item.isChecked = !item.isChecked
        uiState.selectButton.isEnabled = options.any { it.checkBox.isChecked }
        if (pickArgs.choice == Choice.SINGLE) {
            onItemsPicked()
        }
    }
}