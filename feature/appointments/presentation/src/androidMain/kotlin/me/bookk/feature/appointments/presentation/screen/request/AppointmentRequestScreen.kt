package me.bookk.feature.appointments.presentation.screen.request

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import me.bookk.android.feature.appointments.resources.AppointmentsRes
import me.bookk.designsystem.components.ActionButton
import me.bookk.designsystem.components.AppCard
import me.bookk.designsystem.components.List
import me.bookk.designsystem.components.StateTextButton
import me.bookk.designsystem.components.stateButtonColors
import me.bookk.designsystem.theme.color.LocalColors
import me.bookk.designsystem.theme.typography.primary
import me.bookk.designsystem.theme.typography.secondary

private const val TINT_ALPHA = 0.15f

@Composable
internal fun AppointmentRequestContent(state: AppointmentRequestState) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        RequestsHeader(state)
        List(state.requests, modifier = Modifier.weight(1f)) {
            RequestItemCard(
                modifier = Modifier
                    .animateItem()
                    .padding(vertical = 4.dp),
                item = it,
            )
        }
    }
}

@Composable
private fun RequestsHeader(state: AppointmentRequestState) {
    Text(
        text = state.title.localized(),
        style = MaterialTheme.typography.titleLarge.primary(),
        fontWeight = FontWeight.SemiBold,
    )
}

@Composable
private fun RequestItemCard(modifier: Modifier = Modifier, item: AppointmentRequestItemState) {
    AppCard(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            ClientRow(item)
            ServicesRow(item)
            if (item.note.isNotBlank()) {
                Text(
                    text = item.note,
                    style = MaterialTheme.typography.bodyMedium.secondary(),
                    fontStyle = FontStyle.Italic,
                )
            }
            RequestActions(item)
        }
    }
}

@Composable
private fun ClientRow(item: AppointmentRequestItemState) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        InitialsAvatar(item.clientInitials)
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = item.clientName,
                style = MaterialTheme.typography.titleMedium.primary(),
                fontWeight = FontWeight.SemiBold,
            )
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
                    text = item.schedule.localized(),
                    style = MaterialTheme.typography.bodyMedium.secondary(),
                )
            }
        }
        item.onCallClick?.let { onCallClick ->
            IconButton(onClick = onCallClick) {
                Icon(
                    imageVector = Icons.Outlined.Phone,
                    contentDescription = AppointmentsRes.strings.appointments_details_phone
                        .desc().localized(),
                    tint = LocalColors.current.actionText,
                )
            }
        }
    }
}

@Composable
private fun InitialsAvatar(initials: String) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .background(LocalColors.current.actionText.copy(alpha = TINT_ALPHA), CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = initials,
            style = MaterialTheme.typography.titleSmall,
            color = LocalColors.current.actionText,
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ServicesRow(item: AppointmentRequestItemState) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        FlowRow(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            item.services.forEach { ServiceChip(it.localized()) }
        }
        Text(
            text = item.price,
            style = MaterialTheme.typography.titleSmall.primary(),
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun ServiceChip(text: String) {
    Text(
        modifier = Modifier
            .background(LocalColors.current.background, MaterialTheme.shapes.small)
            .border(1.dp, LocalColors.current.divider, MaterialTheme.shapes.small)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        text = text,
        style = MaterialTheme.typography.labelMedium.secondary(),
    )
}

@Composable
private fun RequestActions(item: AppointmentRequestItemState) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        StateTextButton(
            modifier = Modifier
                .weight(1f)
                .border(1.dp, LocalColors.current.divider, MaterialTheme.shapes.large),
            state = item.declineButton,
            colors = ButtonDefaults.stateButtonColors(
                contentColor = LocalColors.current.error,
            ),
        )
        ActionButton(
            modifier = Modifier.weight(1f),
            state = item.approveButton,
        )
    }
}
