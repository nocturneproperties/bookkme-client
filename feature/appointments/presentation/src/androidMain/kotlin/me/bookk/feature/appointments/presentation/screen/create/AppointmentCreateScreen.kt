package me.bookk.feature.appointments.presentation.screen.create

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import library.picker.standardOptionsScreenPicker
import me.bookk.designsystem.components.ActionButton
import me.bookk.designsystem.components.AppTopBar
import me.bookk.designsystem.components.DatePickerField
import me.bookk.designsystem.components.OptionsMultiPicker
import me.bookk.designsystem.components.TextField
import me.bookk.designsystem.components.TimePickerField
import me.bookk.designsystem.components.singleClickInteractionSource
import me.bookk.designsystem.modifier.topShadow
import me.bookk.designsystem.theme.color.LocalColors
import me.bookk.feature.appointments.presentation.navigation.ClientPicker

@Composable
internal fun AppointmentCreateScreen(state: AppointmentCreateState, clientPicker: ClientPicker) {
    Scaffold(
        topBar = { AppTopBar(state = state.appBar) },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .topShadow(24.dp, colorFrom = LocalColors.current.background)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            ClientField(state, Modifier.padding(top = 16.dp))
            OptionsMultiPicker(
                state.servicePicker,
                showDividers = true,
                screenPicker = standardOptionsScreenPicker(),
            ) { item, onItemRemove ->
                ServiceItem(item, onItemRemove)
            }
            if (state.subtotalPrice.isNotEmpty()) {
                SubtotalRow(state.subtotalLabel, state.subtotalPrice)
            }
            DatePickerField(state.datePicker)
            TimePickerField(state.timePicker)
            TextField(state.note, minLines = 3)
            ActionButton(
                state.create,
                Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
            )
        }
    }
    if (state.isClientPickerVisible) {
        clientPicker(state.onClientPicked) { state.isClientPickerVisible = false }
    }
}

@Composable
private fun ClientField(state: AppointmentCreateState, modifier: Modifier) {
    TextField(
        modifier = modifier,
        interactionSource = singleClickInteractionSource { state.onClientFieldClick() },
        trailingIcon = {
            Icon(
                imageVector = Icons.Filled.ChevronRight,
                tint = LocalColors.current.secondaryText,
                contentDescription = null,
            )
        },
        state = state.clientField,
    )
}
