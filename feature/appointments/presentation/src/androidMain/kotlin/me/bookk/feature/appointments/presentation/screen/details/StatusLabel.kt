package me.bookk.feature.appointments.presentation.screen.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.localized
import me.bookk.designsystem.resources.color.themed

@Composable
internal fun StatusLabel(status: UIAppointmentStatus, modifier: Modifier = Modifier) {
    val color = status.color.themed
    Text(
        text = status.label.localized(),
        modifier = modifier
            .background(color = color.copy(alpha = 0.12f), shape = MaterialTheme.shapes.small)
            .padding(horizontal = 12.dp, vertical = 4.dp),
        style = MaterialTheme.typography.labelLarge,
        color = color
    )
}
