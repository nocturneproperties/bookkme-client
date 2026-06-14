package me.bookk.feature.appointments.presentation.screen.create

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.localized
import me.bookk.designsystem.components.ActionButton
import me.bookk.designsystem.components.AppTopBar
import me.bookk.designsystem.components.DatePickerField
import me.bookk.designsystem.components.MultiPicker
import me.bookk.designsystem.components.PickerField
import me.bookk.designsystem.components.TextField
import me.bookk.designsystem.components.TimePickerField
import me.bookk.designsystem.modifier.topShadow
import me.bookk.designsystem.theme.color.AppColors
import me.bookk.designsystem.theme.color.LocalColors
import me.bookk.designsystem.theme.typography.primary
import me.bookk.designsystem.theme.typography.secondary

@Composable
internal fun AppointmentCreateScreen(
    state: AppointmentCreateState
) {
    Scaffold(
        topBar = { AppTopBar(state = state.appBar) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .topShadow(24.dp, colorFrom = LocalColors.current.background)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            PickerField(state.clientPicker, modifier = Modifier.padding(top = 16.dp))
            MultiPicker(state.servicePicker) { item, onItemRemove ->
                ServiceItem(item, onItemRemove)
            }
            DatePickerField(state.datePicker)
            TimePickerField(state.timePicker)
            TextField(state.note, minLines = 3)
            ActionButton(
                state.create, Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            )
        }
    }
}

@Composable
private fun ServiceItem(
    service: ServicePickerPresentation,
    onItemRemove: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .padding(vertical = 16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.padding(start = 16.dp)) {
                Text(
                    service.displayName.localized(),
                    style = MaterialTheme.typography.titleMedium.primary()
                )
                Text(
                    service.duration.localized(),
                    style = MaterialTheme.typography.labelMedium.secondary()
                )
            }
            Spacer(Modifier.weight(1f))
            Text(service.price, style = MaterialTheme.typography.titleMedium.primary())
            TextButton(onClick = { onItemRemove() }) {
                Box(
                    modifier = Modifier
                        .background(AppColors.White.copy(alpha = 0.1f), CircleShape)
                        .size(28.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "-",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyLarge.secondary(),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        HorizontalDivider(color = LocalColors.current.divider)
    }
}
