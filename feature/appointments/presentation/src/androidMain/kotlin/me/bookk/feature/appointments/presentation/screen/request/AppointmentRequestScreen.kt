package me.bookk.feature.appointments.presentation.screen.request

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.ModeComment
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import me.bookk.designsystem.components.ActionButton
import me.bookk.designsystem.components.AppCard
import me.bookk.designsystem.components.List
import me.bookk.designsystem.components.StateTextButton
import me.bookk.designsystem.components.stateButtonColors
import me.bookk.designsystem.theme.color.LocalColors
import me.bookk.designsystem.theme.typography.primary
import me.bookk.designsystem.theme.typography.secondary

@Composable
internal fun AppointmentRequestContent(
    state: AppointmentRequestState
) {
    List(state.requests, modifier = Modifier.fillMaxSize()) {
        RequestItemCard(
            modifier = Modifier
                .animateItem()
                .padding(vertical = 4.dp),
            item = it,
        )
    }
}

@Composable
private fun RequestItemCard(
    modifier: Modifier = Modifier,
    item: AppointmentRequestItemState,
) {
    AppCard(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = item.clientName,
                    style = MaterialTheme.typography.titleMedium.primary(),
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = item.serviceName,
                    style = MaterialTheme.typography.bodyMedium.secondary(),
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.CalendarToday,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = LocalColors.current.secondaryText,
                    )
                    Text(
                        text = item.scheduledDate,
                        style = MaterialTheme.typography.bodySmall.secondary(),
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Schedule,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = LocalColors.current.secondaryText,
                    )
                    Text(
                        text = item.scheduledTime,
                        style = MaterialTheme.typography.bodySmall.secondary(),
                    )
                }
            }

            if (item.note.isNotBlank()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .background(
                            color = LocalColors.current.primaryText.copy(alpha = 0.05f),
                            shape = MaterialTheme.shapes.small
                        )
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.Top,
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ModeComment,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = LocalColors.current.secondaryText,
                    )
                    Text(
                        text = item.note,
                        style = MaterialTheme.typography.bodySmall.secondary(),
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                StateTextButton(
                    modifier = Modifier.weight(1f),
                    state = item.declineButton,
                    colors = ButtonDefaults.stateButtonColors(
                        contentColor = LocalColors.current.error
                    )
                )
                ActionButton(
                    modifier = Modifier.weight(1f),
                    state = item.approveButton,
                )
            }
        }
    }
}

