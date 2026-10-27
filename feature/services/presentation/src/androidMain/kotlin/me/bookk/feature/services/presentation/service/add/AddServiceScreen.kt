package me.bookk.feature.services.presentation.service.add

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import library.picker.standardScreenPicker
import me.bookk.designsystem.components.ActionButton
import me.bookk.designsystem.components.AppTopBar
import me.bookk.designsystem.components.CheckBox
import me.bookk.designsystem.components.CollapsingAppBarScaffold
import me.bookk.designsystem.components.PickerField
import me.bookk.designsystem.components.TextField

@Composable
internal fun AddServiceScreen(state: AddServiceState) {
    CollapsingAppBarScaffold(
        modifier = Modifier
            .systemBarsPadding()
            .imePadding(),
        topBar = {
            AppTopBar(
                state = state.appBar,
                behavior = it
            )
        },
        content = { pv ->
            Column(
                modifier = Modifier
                    .padding(pv)
                    .padding(all = 16.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PickerField(
                    state = state.group,
                    screenPicker = standardScreenPicker()
                )
                TextField(state.name)
                TextField(state.duration)
                TextField(state.price)
                CheckBox(state.enabled)
            }
        },
        bottomBar = {
            ActionButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                state = state.create
            )
        }
    )
}