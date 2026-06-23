package me.bookk.feature.appointments.presentation.screen.requestlist

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalDate
import me.bookk.core.now
import me.bookk.core.presentation.date.today
import me.bookk.designsystem.components.AppCard
import me.bookk.designsystem.components.AppDatePicker
import me.bookk.designsystem.components.AppTopBar
import me.bookk.designsystem.components.List
import me.bookk.designsystem.components.PullToRefresh
import me.bookk.designsystem.theme.color.LocalColors
import me.bookk.designsystem.theme.typography.primary
import me.bookk.designsystem.theme.typography.secondary

@Composable
internal fun AppointmentListScreen(
    state: AppointmentListState
) {
    Scaffold(
        modifier = Modifier
            .systemBarsPadding()
            .imePadding(),
        topBar = {
            Column {
                AppTopBar(state = state.appBar)
                DateStrip(
                    dates = state.dates.items,
                    selectedDate = state.datePicker.pickedDate ?: LocalDate.now(),
                    onDateSelected = { state.datePicker.onDatePicked?.invoke(it) }
                )
                HorizontalDivider(color = LocalColors.current.divider)
            }
        }
    ) { pv ->
        if (state.datePicker.isDatePickerVisible) {
            AppDatePicker(state.datePicker)
        }
        Column(
            modifier = Modifier
                .padding(pv)
                .fillMaxSize()
        ) {
            PullToRefresh(state = state.refresh) {
                List(
                    modifier = Modifier.fillMaxSize(),
                    state = state.appointments,
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    idProvider = { it.source.id }
                ) {
                    AppointmentRequestItem(Modifier.animateItem(), it)
                }
            }
        }
    }
}

@Composable
private fun DateStrip(
    dates: List<DateInfo>,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    Row {
        dates.forEach { info ->
            DateCell(
                modifier = Modifier.weight(1f),
                date = info.date,
                isSelected = info.date == selectedDate,
                onClick = { onDateSelected(info.date) }
            )
        }
    }
}

@Composable
private fun DateCell(
    modifier: Modifier,
    date: LocalDate,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val colors = LocalColors.current
    val background = if (isSelected) colors.buttonPrimary else colors.background
    val isToday = remember(date) { date == LocalDate.today() }
    val borderColor = if (isToday) LocalColors.current.actionText else Color.Transparent
    val animatedBg by animateColorAsState(background)
    Column(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .border(1.dp, borderColor, MaterialTheme.shapes.medium)
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = date.dayOfWeek.name.take(1),
            style = MaterialTheme.typography.labelSmall,
            color = LocalColors.current.primaryText
        )
        Text(
            modifier = Modifier
                .size(30.dp, 30.dp)
                .background(animatedBg, CircleShape)
                .wrapContentHeight(align = Alignment.CenterVertically),
            text = date.day.toString(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
            color = LocalColors.current.primaryText
        )
    }
}

@Composable
private fun AppointmentRequestItem(
    modifier: Modifier = Modifier,
    state: AppointmentItemState
) {
    AppCard(
        modifier = modifier.fillMaxWidth(),
        onClick = state.onItemClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = state.scheduledAt,
                style = MaterialTheme.typography.titleLarge.primary(),
                fontWeight = FontWeight.Medium
            )
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = state.clientName,
                    style = MaterialTheme.typography.titleMedium.primary()
                )
                Text(
                    text = state.serviceName,
                    style = MaterialTheme.typography.bodyMedium.secondary()
                )
            }
            Text(
                text = state.earnings,
                style = MaterialTheme.typography.titleSmall.primary()
            )
        }
    }
}
