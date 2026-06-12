package me.bookk.feature.appointments.presentation.screen.requestlist

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import me.bookk.designsystem.components.AppCard
import me.bookk.designsystem.components.AppTopBar
import me.bookk.designsystem.components.List
import me.bookk.designsystem.components.PullToRefresh
import me.bookk.designsystem.theme.color.LocalColors
import me.bookk.designsystem.theme.typography.primary
import me.bookk.designsystem.theme.typography.secondary
import me.bookk.feature.appointments.domain.api.entity.AppointmentStatus

private const val DATE_STRIP_HALF_RANGE = 30

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
                    state = state.requests,
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
        (-DATE_STRIP_HALF_RANGE..DATE_STRIP_HALF_RANGE).map { offset ->
            when {
                offset < 0 -> selectedDate.minus(-offset, DateTimeUnit.DAY)
                offset > 0 -> selectedDate.plus(offset, DateTimeUnit.DAY)
                else -> selectedDate
            }
        }
    }
    val listState = rememberLazyListState()

    LaunchedEffect(selectedDate) {
        listState.scrollToItem((DATE_STRIP_HALF_RANGE - 3).coerceAtLeast(0))
    }

    LazyRow(
        state = listState,
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(dates, key = { it.toString() }) { date ->
            DateCell(
                date = date,
                isSelected = date == selectedDate,
                onClick = { onDateSelected(date) }
            )
        }
    }
}

@Composable
private fun DateCell(
    date: LocalDate,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val colors = LocalColors.current
    val background = if (isSelected) colors.buttonPrimary else colors.background
    val textColor = if (isSelected) colors.background else colors.primaryText
    val labelColor = if (isSelected) colors.background else colors.secondaryText

    Column(
        modifier = Modifier
            .size(width = 44.dp, height = 60.dp)
            .clip(MaterialTheme.shapes.medium)
            .background(background)
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = date.dayOfWeek.name.take(3),
            style = MaterialTheme.typography.labelSmall,
            color = labelColor
        )
        Text(
            text = date.dayOfMonth.toString(),
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
            color = textColor
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
