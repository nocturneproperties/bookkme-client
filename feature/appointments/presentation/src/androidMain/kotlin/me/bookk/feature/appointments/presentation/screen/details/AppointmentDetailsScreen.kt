package me.bookk.feature.appointments.presentation.screen.details

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.StringDesc
import me.bookk.designsystem.components.ActionButton
import me.bookk.designsystem.components.AppTopBar
import me.bookk.designsystem.components.DateTimePicker
import me.bookk.designsystem.components.InfoCard
import me.bookk.designsystem.components.InfoCardLine
import me.bookk.designsystem.components.List
import me.bookk.designsystem.components.OptionsMultiPicker
import me.bookk.designsystem.components.StateTextButton
import me.bookk.designsystem.components.stateButtonColors
import me.bookk.designsystem.theme.color.LocalColors
import me.bookk.designsystem.theme.typography.primary
import me.bookk.designsystem.theme.typography.secondary
import me.bookk.designsystem.uistate.ButtonState
import me.bookk.designsystem.uistate.OptionsMultiPickerState
import me.bookk.designsystem.uistate.simple.InfoLine
import me.bookk.feature.appointments.presentation.screen.create.ServiceItem
import me.bookk.feature.appointments.presentation.screen.create.ServicePickerPresentation

private const val PRICE_ADJUSTMENT_REASON_ID = "price_adjustment_reason"

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
        content = { pv ->
            Column(
                modifier = Modifier
                    .padding(pv)
                    .fillMaxSize(),
            ) {
                StatusRow(state.status, state.statusNote, state.noShowButton, state.rescheduleButton)
                HorizontalDivider(
                    Modifier.padding(top = 4.dp),
                    color = LocalColors.current.divider
                )
                val lines = state.infoSections.items
                List(
                    state.infoSections,
                    contentPadding = PaddingValues(top = 16.dp),
                    footer = {
                        Column {
                            ServicesSection(state.servicePicker)
                            PriceAdjustmentSection(state.priceAdjustment)
                            state.priceAdjustment.reason?.let {
                                PriceAdjustmentReasonSection(state.priceAdjustment.reasonTitle, it)
                            }
                            TotalRow(state.totalLabel, state.totalPrice, state.originalPrice)
                            CompleteAction(state.completeButton)
                        }
                    }
                ) {
                    InfoCardLine(
                        line = it,
                        isFirst = it.id == lines.first().id,
                        isLast = it.id == lines.last().id,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
            if (state.dateTimePicker.isDatePickerVisible) {
                DateTimePicker(state.dateTimePicker)
            }
            AppointmentCompletionSheet(state.completion)
        }
    )
}

@Composable
private fun CompleteAction(completeButton: ButtonState) {
    if (!completeButton.isVisible) return
    ActionButton(
        completeButton,
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    )
}

@Composable
private fun ServicesSection(servicePicker: OptionsMultiPickerState<ServicePickerPresentation>) {
    if (!servicePicker.isVisible) return
    OptionsMultiPicker(servicePicker, Modifier.padding(16.dp), showDividers = true) { item, _ ->
        ServiceItem(item, onItemRemove = null)
    }
}

@Composable
private fun PriceAdjustmentSection(state: PriceAdjustmentSectionState) {
    if (!state.isVisible) return
    OptionsMultiPicker(
        state.servicePicker,
        Modifier.padding(horizontal = 16.dp).padding(top = 8.dp),
        showDividers = true
    ) { item, _ ->
        ServiceItem(item, onItemRemove = null)
    }
}

@Composable
private fun TotalRow(label: StringDesc, price: String, originalPrice: String?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(top = 16.dp, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label.localized(),
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.titleLarge.primary(),
            fontWeight = FontWeight.Bold
        )
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = price,
                style = MaterialTheme.typography.titleLarge.primary(),
                fontWeight = FontWeight.Bold
            )
            originalPrice?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall.secondary(),
                    textDecoration = TextDecoration.LineThrough
                )
            }
        }
    }
}

@Composable
private fun PriceAdjustmentReasonSection(title: StringDesc, reason: StringDesc) {
    InfoCard(
        lines = listOf(InfoLine(id = PRICE_ADJUSTMENT_REASON_ID, title = title, value = reason)),
        modifier = Modifier.padding(horizontal = 16.dp).padding(top = 16.dp)
    )
}

@Composable
private fun StatusRow(
    status: UIAppointmentStatus,
    statusNote: StringDesc?,
    noShowButton: ButtonState,
    rescheduleButton: ButtonState
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 4.dp)
            .heightIn(min = 48.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        StatusLabel(status = status)
        statusNote?.let {
            Text(
                text = it.localized(),
                modifier = Modifier.padding(start = 8.dp),
                style = MaterialTheme.typography.bodyMedium.secondary()
            )
        }
        Spacer(Modifier.weight(1f))
        if (noShowButton.isVisible) {
            StateTextButton(
                noShowButton,
                colors = ButtonDefaults.stateButtonColors(contentColor = LocalColors.current.error)
            )
        }
        if (rescheduleButton.isVisible) {
            StateTextButton(rescheduleButton)
        }
    }
}
