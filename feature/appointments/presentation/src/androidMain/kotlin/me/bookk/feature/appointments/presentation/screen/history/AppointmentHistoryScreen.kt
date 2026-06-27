package me.bookk.feature.appointments.presentation.screen.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.bookk.designsystem.components.AppCard
import me.bookk.designsystem.components.AppTopBar
import me.bookk.designsystem.components.List
import me.bookk.designsystem.components.PullToRefresh
import me.bookk.designsystem.components.TextField
import me.bookk.designsystem.theme.typography.primary
import me.bookk.designsystem.theme.typography.secondary
import me.bookk.feature.appointments.presentation.screen.details.StatusLabel

@Composable
internal fun AppointmentHistoryScreen(
    state: AppointmentHistoryState
) {
    Scaffold(
        modifier = Modifier
            .systemBarsPadding()
            .imePadding(),
        topBar = {
            Column {
                AppTopBar(state = state.appBar)
                TextField(
                    state.searchField,
                    Modifier
                        .padding(horizontal = 16.dp)
                        .padding(top = 16.dp)
                )
            }
        }
    ) { pv ->
        PullToRefresh(
            state = state.refresh,
            modifier = Modifier
                .padding(pv)
                .fillMaxSize()
        ) {
            List(
                modifier = Modifier.fillMaxSize(),
                state = state.appointments,
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                idProvider = { it.source.id }
            ) {
                AppointmentHistoryItem(Modifier.animateItem(), it)
            }
        }
    }
}

@Composable
private fun AppointmentHistoryItem(
    modifier: Modifier = Modifier,
    state: AppointmentHistoryItemState
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
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = state.clientName,
                    style = MaterialTheme.typography.titleMedium.primary()
                )
                Text(
                    text = state.serviceName,
                    style = MaterialTheme.typography.bodyMedium.secondary()
                )
                Text(
                    text = state.scheduledAt,
                    style = MaterialTheme.typography.bodySmall.secondary()
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = state.earnings,
                    style = MaterialTheme.typography.titleSmall.primary()
                )
                StatusLabel(state.status)
            }
        }
    }
}
