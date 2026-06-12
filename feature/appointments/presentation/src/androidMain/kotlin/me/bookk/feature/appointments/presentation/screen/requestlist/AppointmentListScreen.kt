package me.bookk.feature.appointments.presentation.screen.requestlist

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.plus
import me.bookk.core.presentation.date.startOfWeek
import me.bookk.designsystem.components.AppCard
import me.bookk.designsystem.components.AppTopBar
import me.bookk.designsystem.components.List
import me.bookk.designsystem.components.PullToRefresh
import me.bookk.designsystem.theme.color.LocalColors
import me.bookk.designsystem.theme.typography.primary
import me.bookk.designsystem.theme.typography.secondary
import me.bookk.feature.appointments.domain.api.entity.AppointmentStatus

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
                    selectedDate = state.selectedDate,
                    onDateSelected = { state.selectedDate = it }
                )
                HorizontalDivider(color = LocalColors.current.divider)
            }
        }
    ) { pv ->
        Column(
            modifier = Modifier
                .padding(pv)
                .fillMaxSize()
        ) {
            PullToRefresh(state = state.refresh) {
                List(
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
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    val dates = remember(selectedDate) {
        val start = selectedDate.startOfWeek()
        (DayOfWeek.MONDAY.isoDayNumber..DayOfWeek.SUNDAY.isoDayNumber).toList().map {
            start.plus(it - 1, DateTimeUnit.DAY)
        }
    }
    Row {
        dates.forEach { date ->
            DateCell(
                modifier = Modifier.weight(1f),
                date = date,
                isSelected = date == selectedDate,
                onClick = { onDateSelected(date) }
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
    val animatedBg by animateColorAsState(background)
    Column(
        modifier = modifier
            .padding(vertical = 8.dp)
            .clip(MaterialTheme.shapes.medium)
            .clickable { onClick() },
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
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = state.clientName,
                    style = MaterialTheme.typography.titleMedium.primary()
                )
                StatusBadge(status = state.source.status)
            }
            Text(
                text = state.serviceName,
                style = MaterialTheme.typography.bodyMedium.secondary()
            )
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = LocalColors.current.divider
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = state.scheduledAt,
                    style = MaterialTheme.typography.bodySmall.secondary()
                )
                Text(
                    text = state.earnings,
                    style = MaterialTheme.typography.titleSmall.primary()
                )
            }
        }
    }
}

@Composable
private fun StatusBadge(status: AppointmentStatus) {
    val colors = LocalColors.current
    val (background, textColor) = when (status) {
        AppointmentStatus.SCHEDULED -> colors.buttonActive.copy(alpha = 0.15f) to colors.buttonActive
        AppointmentStatus.COMPLETED -> colors.success.copy(alpha = 0.15f) to colors.success
        AppointmentStatus.CANCELLED -> colors.error.copy(alpha = 0.15f) to colors.error
    }
    val label = when (status) {
        AppointmentStatus.SCHEDULED -> "Scheduled"
        AppointmentStatus.COMPLETED -> "Completed"
        AppointmentStatus.CANCELLED -> "Cancelled"
    }
    Text(
        modifier = Modifier
            .clip(MaterialTheme.shapes.small)
            .background(background)
            .padding(horizontal = 8.dp, vertical = 2.dp),
        text = label,
        style = MaterialTheme.typography.labelSmall,
        color = textColor
    )
}
