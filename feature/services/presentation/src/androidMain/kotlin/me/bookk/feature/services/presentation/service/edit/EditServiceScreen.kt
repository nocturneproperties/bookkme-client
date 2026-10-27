package me.bookk.feature.services.presentation.service.edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import library.picker.standardScreenPicker
import me.bookk.designsystem.components.ActionButton
import me.bookk.designsystem.components.AppTopBar
import me.bookk.designsystem.components.CheckBox
import me.bookk.designsystem.components.PickerField
import me.bookk.designsystem.components.StateTextButton
import me.bookk.designsystem.components.TextField
import me.bookk.designsystem.components.stateButtonColors
import me.bookk.designsystem.theme.color.LocalColors

@Composable
internal fun EditServiceScreen(state: EditServiceState) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .imePadding(),
        topBar = {
            AppTopBar(state = state.appBar)
        },
        content = { pv ->
            Column(
                modifier = Modifier
                    .padding(pv)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                PickerField(
                    state = state.group,
                    screenPicker = standardScreenPicker()
                )
                TextField(state.name, keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next))
                TextField(state.duration, keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next))
                TextField(state.price, keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done))
                CheckBox(state.available)
            }
        },
        bottomBar = {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (state.submit.isVisible) {
                    ActionButton(
                        modifier = Modifier.fillMaxWidth(),
                        state = state.submit
                    )
                }
                if (state.deleteButton.isVisible) {
                    StateTextButton(
                        modifier = Modifier.fillMaxWidth(),
                        state = state.deleteButton,
                        colors = ButtonDefaults.stateButtonColors(contentColor = LocalColors.current.error)
                    )
                }
            }
        }
    )
}
