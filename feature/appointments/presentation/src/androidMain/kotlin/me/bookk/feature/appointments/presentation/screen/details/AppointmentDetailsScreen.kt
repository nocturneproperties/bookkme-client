package me.bookk.feature.appointments.presentation.screen.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.bookk.designsystem.components.ActionButton
import me.bookk.designsystem.components.AppTopBar
import me.bookk.designsystem.components.DateTimePicker
import me.bookk.designsystem.components.InfoSection
import me.bookk.designsystem.components.List
import me.bookk.designsystem.components.OptionsMultiPicker
import me.bookk.designsystem.components.StateTextButton
import me.bookk.designsystem.components.stateButtonColors
import me.bookk.designsystem.theme.color.LocalColors
import me.bookk.designsystem.uistate.ButtonState
import me.bookk.feature.appointments.presentation.screen.create.ServiceItem
import me.bookk.feature.appointments.presentation.screen.create.SubtotalRow

@Composable
internal fun AppointmentDetailsScreen(state: AppointmentDetailsState) {
    Scaffold(
        modifier = Modifier
            .systemBarsPadding()
            .imePadding(),
        topBar = {
            Column {
                AppTopBar(state = state.appBar)
            }
        },
        bottomBar = {
            CompletionActions(state.completeButton, state.noShowButton)
        },
        content = { pv ->
            Column(
                modifier = Modifier
                    .padding(pv)
                    .fillMaxSize(),
            ) {
                StatusLabel(
                    status = state.status,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
                HorizontalDivider(
                    Modifier.padding(top = 4.dp),
                    color = LocalColors.current.divider
                )
                List(
                    state.infoSections,
                    footer = {
                        Column {
                            RescheduleAction(state.rescheduleButton)
                            ServicesSection(state)
                        }
                    }
                ) {
                    InfoSection(it)
                }
            }
            if (state.dateTimePicker.isDatePickerVisible) {
                DateTimePicker(state.dateTimePicker)
            }
        }
    )
}

@Composable
private fun CompletionActions(completeButton: ButtonState, noShowButton: ButtonState) {
    if (!completeButton.isVisible && !noShowButton.isVisible) return
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (completeButton.isVisible) {
            ActionButton(completeButton, modifier = Modifier.fillMaxWidth())
        }
        if (noShowButton.isVisible) {
            StateTextButton(
                noShowButton,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.stateButtonColors(contentColor = LocalColors.current.error)
            )
        }
    }
}

@Composable
private fun ServicesSection(state: AppointmentDetailsState) {
    if (!state.servicePicker.isVisible) return
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OptionsMultiPicker(state.servicePicker) { item, _ ->
            ServiceItem(item, onItemRemove = null)
        }
        if (state.subtotalPrice.isNotEmpty()) {
            SubtotalRow(state.subtotalLabel, state.subtotalPrice)
        }
    }
}

@Composable
private fun RescheduleAction(rescheduleButton: ButtonState) {
    if (!rescheduleButton.isVisible) return
    ActionButton(
        rescheduleButton,
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .padding(top = 16.dp)
            .fillMaxWidth()
    )
}
