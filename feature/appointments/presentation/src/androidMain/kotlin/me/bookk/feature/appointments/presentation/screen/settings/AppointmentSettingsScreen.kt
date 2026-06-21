package me.bookk.feature.appointments.presentation.screen.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.twotone.CalendarToday
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import me.bookk.android.feature.appointments.resources.AppointmentsRes
import me.bookk.designsystem.components.ActionButton
import me.bookk.designsystem.components.AlignStartTextButton
import me.bookk.designsystem.components.AppCard
import me.bookk.designsystem.components.AppTopBar
import me.bookk.designsystem.components.DateRangePicker
import me.bookk.designsystem.components.Header
import me.bookk.designsystem.components.MultiPicker
import me.bookk.designsystem.components.StateSwitch
import me.bookk.designsystem.components.TextField
import me.bookk.designsystem.components.TimePickerField
import me.bookk.designsystem.resources.DesignSystem
import me.bookk.designsystem.theme.color.LocalColors
import me.bookk.designsystem.theme.typography.secondary

@Composable
internal fun AppointmentSettingsScreen(
    state: AppointmentSettingsState
) {
    Scaffold(
        modifier = Modifier.imePadding(),
        topBar = { AppTopBar(state = state.appBar) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ScheduleStrip(state.schedule)

            MultiPicker(
                state.dayOffs,
                pickerContent = {
                    DateRangePicker(
                        state = state.dateRange,
                        onDismiss = { state.dayOffs.isPickerVisible = false }
                    )
                },
                itemContent = { item, onItemRemove ->
                    DayOffItem(item, onItemRemove)
                }
            )
            RequestsSettings(state)

            ActionButton(
                state.save,
                Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            )
        }
    }
}

@Composable
private fun DayOffItem(
    item: DateRangePickerPresentation,
    onDeleteClick: () -> Unit
) {
    Row(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(Icons.TwoTone.CalendarToday, contentDescription = null)
        Text(
            item.displayName.localized(),
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.titleMedium
        )
        IconButton(onClick = { onDeleteClick() }) {
            Icon(
                Icons.Filled.Delete,
                contentDescription = DesignSystem.strings.action_delete.desc().localized(),
                tint = LocalColors.current.error
            )
        }
    }
}

@Composable
private fun RequestsSettings(state: AppointmentSettingsState) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Column {
            Header(AppointmentsRes.strings.appointments_settings_requests.desc().localized())
            StateSwitch(state.automaticApproval, modifier = Modifier.fillMaxWidth())
        }
        TextField(state.minimalBreak)
        TextField(state.note, minLines = 3)
    }
}

@Composable
private fun ScheduleStrip(schedule: ScheduleState) {
    val days = remember(schedule) { schedule.asList() }
    var selectedState by remember(schedule) { mutableStateOf(schedule.monday) }
    Column(Modifier.animateContentSize()) {
        Header(AppointmentsRes.strings.appointments_settings_schedule.desc().localized())
        Row {
            days.forEach { day ->
                DayOfWeekCell(
                    modifier = Modifier.weight(1f),
                    state = day,
                    onClick = {
                        day.isVisible = !day.isVisible
                        days.forEach {
                            if (it !== day) it.isVisible = false
                        }
                        selectedState = day
                    }
                )
            }
        }
        AnimatedVisibility(selectedState.isVisible) {
            ScheduleDay(selectedState)
        }
    }
}

@Composable
private fun DayOfWeekCell(
    modifier: Modifier,
    state: DaySettingsState,
    onClick: () -> Unit
) {
    val colors = LocalColors.current
    val background = if (state.isActive.isChecked) colors.buttonPrimary else Color.Transparent
    val animatedBg by animateColorAsState(background)
    Column(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = state.dayIndicator.localized(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .background(animatedBg, CircleShape)
                .border(
                    width = if (state.isVisible) 2.dp else 0.dp,
                    color = LocalColors.current.primaryText,
                    shape = CircleShape
                )
                .size(40.dp)
                .wrapContentHeight(align = Alignment.CenterVertically)
        )
    }
}

@Composable
private fun ScheduleDay(state: DaySettingsState) {
    AppCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
    ) {
        Column(Modifier.animateContentSize()) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                val bgColor by animateColorAsState(if (state.isActive.isChecked) LocalColors.current.actionText else LocalColors.current.inactive)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = state.dayIndicator.localized(),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .background(bgColor, CircleShape)
                            .size(40.dp)
                            .wrapContentHeight(align = Alignment.CenterVertically)
                    )
                    Text(
                        state.title.localized(),
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.titleMedium
                    )
                    StateSwitch(state.isActive)
                }
            }
            state.intervals.forEach { interval ->
                key(interval) {
                    TimeRow(
                        modifier = Modifier.padding(bottom = 8.dp, start = 16.dp),
                        timeSettings = interval,
                        onDeleteClick = { state.onDeleteInterval(interval) }
                    )
                }
            }
            AlignStartTextButton(
                state.addTimeButton,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun TimeRow(
    modifier: Modifier,
    timeSettings: TimeSettingState,
    onDeleteClick: () -> Unit
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {

        TimePickerField(timeSettings.timeFromPicker, modifier = Modifier.weight(1f))

        Text(
            "-",
            modifier = Modifier.padding(horizontal = 8.dp),
            style = MaterialTheme.typography.titleSmall.secondary()
        )

        TimePickerField(timeSettings.timeToPicker, modifier = Modifier.weight(1f))

        IconButton(onClick = onDeleteClick) {
            Icon(Icons.Filled.Close, contentDescription = null)
        }
    }
}
