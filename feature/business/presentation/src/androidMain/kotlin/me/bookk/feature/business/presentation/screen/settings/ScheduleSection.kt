package me.bookk.feature.business.presentation.screen.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.twotone.CalendarToday
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import me.bookk.android.feature.business.resources.BusinessRes
import me.bookk.designsystem.components.AlignStartTextButton
import me.bookk.designsystem.components.AppCard
import me.bookk.designsystem.components.DateRangePicker
import me.bookk.designsystem.components.Header
import me.bookk.designsystem.components.MultiPicker
import me.bookk.designsystem.components.StateSwitch
import me.bookk.designsystem.components.TimePickerField
import me.bookk.designsystem.resources.DesignSystem
import me.bookk.designsystem.theme.color.LocalColors
import me.bookk.designsystem.theme.typography.secondary
import me.bookk.feature.business.presentation.screen.settings.state.BusinessSettingsState
import me.bookk.feature.business.presentation.screen.settings.state.DateRangePickerPresentation
import me.bookk.feature.business.presentation.screen.settings.state.DaySettingsState
import me.bookk.feature.business.presentation.screen.settings.state.ScheduleState
import me.bookk.feature.business.presentation.screen.settings.state.TimeSettingState

@Composable
internal fun ScheduleSection(state: BusinessSettingsState) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
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
private fun ScheduleStrip(schedule: ScheduleState) {
    var selectedState by remember(schedule) { mutableStateOf(schedule.monday) }
    Column(Modifier.fillMaxWidth()) {
        Header(BusinessRes.strings.business_settings_schedule.desc().localized())
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize()
                .background(
                    LocalColors.current.elevated,
                    MaterialTheme.shapes.large
                )
                .padding(vertical = 8.dp, horizontal = 16.dp)
        ) {
            schedule.list.items.forEach { day ->
                DayOfWeekCell(
                    modifier = Modifier.weight(1f),
                    state = day,
                    onClick = {
                        day.isVisible = !day.isVisible
                        schedule.list.items.forEach {
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
            .clip(MaterialTheme.shapes.large)
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = state.dayIndicator.localized(),
            textAlign = TextAlign.Center,
            color = if (state.isActive.isChecked) LocalColors.current.onAction else LocalColors.current.primaryText,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .background(animatedBg, CircleShape)
                .border(
                    width = if (state.isVisible) 2.dp else 0.dp,
                    color = if (state.isVisible) LocalColors.current.primaryText else Color.Transparent,
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
                val bgColor by animateColorAsState(
                    if (state.isActive.isChecked) LocalColors.current.actionText else LocalColors.current.inactive
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = state.dayIndicator.localized(),
                        textAlign = TextAlign.Center,
                        color = if (state.isActive.isChecked) LocalColors.current.onAction else LocalColors.current.primaryText,
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
