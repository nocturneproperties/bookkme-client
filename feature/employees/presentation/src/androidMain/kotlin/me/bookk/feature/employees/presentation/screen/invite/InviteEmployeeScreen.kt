package me.bookk.feature.employees.presentation.screen.invite

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.localized
import me.bookk.designsystem.components.ActionButton
import me.bookk.designsystem.components.AppCard
import me.bookk.designsystem.components.AppTopBar
import me.bookk.designsystem.components.Header
import me.bookk.designsystem.components.List
import me.bookk.designsystem.components.PullToRefresh
import me.bookk.designsystem.components.TextField
import me.bookk.designsystem.resources.color.themed
import me.bookk.designsystem.theme.color.LocalColors
import me.bookk.designsystem.theme.typography.active
import me.bookk.designsystem.theme.typography.primary
import me.bookk.designsystem.theme.typography.secondary

@Composable
fun InviteEmployeeScreen(state: InviteEmployeeState) {
    Scaffold(
        modifier = Modifier
            .systemBarsPadding()
            .imePadding(),
        topBar = { AppTopBar(state = state.appBar) },
        content = { pv ->
            Column(
                modifier = Modifier
                    .padding(pv)
                    .fillMaxSize()
            ) {
                Header(
                    modifier = Modifier.padding(top = 16.dp),
                    text = state.descriptionText.localized()
                )
                AppCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        TextField(state.emailField)
                        ActionButton(
                            modifier = Modifier.fillMaxWidth(),
                            state = state.sendButton
                        )
                    }
                }
                Header(text = state.invitationsHeader.localized())
                HorizontalDivider(color = LocalColors.current.divider)
                PullToRefresh(state.refreshState, modifier = Modifier.weight(1f)) {
                    List(
                        state = state.invitationsList,
                        modifier = Modifier.fillMaxSize(),
                        idProvider = InvitationItem::id
                    ) {
                        InvitationRow(it)
                    }
                }
            }
        }
    )
}

@Composable
private fun InvitationRow(item: InvitationItem) {
    val haptics = LocalHapticFeedback.current
    val onLongPress = item.onLongPress
    Column(Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .let {
                    if (onLongPress != null) {
                        it.combinedClickable(
                            onLongClick = {
                                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                onLongPress()
                            },
                            onClick = {}
                        )
                    } else {
                        it
                    }
                }
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(LocalColors.current.actionText.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = item.initials,
                    style = MaterialTheme.typography.labelMedium.active()
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp)
            ) {
                Text(
                    text = item.email,
                    style = MaterialTheme.typography.bodyLarge.primary(),
                    maxLines = 1
                )
                Text(
                    text = item.sentOn.localized(),
                    style = MaterialTheme.typography.bodySmall.secondary()
                )
            }
            val statusColor = item.status.color.themed
            Text(
                modifier = Modifier
                    .background(
                        color = statusColor.copy(alpha = 0.15f),
                        shape = MaterialTheme.shapes.large
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                text = item.status.label.localized(),
                style = MaterialTheme.typography.labelSmall,
                color = statusColor,
                maxLines = 1
            )
        }
        HorizontalDivider(color = LocalColors.current.divider.copy(alpha = 0.5f))
    }
}
