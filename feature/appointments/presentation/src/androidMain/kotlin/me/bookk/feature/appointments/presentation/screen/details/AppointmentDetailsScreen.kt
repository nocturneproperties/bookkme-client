package me.bookk.feature.appointments.presentation.screen.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.localized
import me.bookk.designsystem.components.AppTopBar
import me.bookk.designsystem.components.InfoSection
import me.bookk.designsystem.components.List
import me.bookk.designsystem.resources.color.themed
import me.bookk.designsystem.theme.color.LocalColors

@Composable
internal fun AppointmentDetailsScreen(state: AppointmentDetailsState) {
    Scaffold(
        modifier = Modifier
            .systemBarsPadding()
            .imePadding(),
        topBar = {
            Column {
                AppTopBar(state = state.appBar)
            }
        },
        content = { pv ->
            Column(
                modifier = Modifier
                    .padding(pv)
                    .fillMaxSize(),
            ) {
                StatusLabel(
                    status = state.status,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
                HorizontalDivider(
                    Modifier.padding(top = 4.dp),
                    color = LocalColors.current.divider
                )
                List(state.infoSections) {
                    InfoSection(it)
                }
            }
        }
    )
}

@Composable
private fun StatusLabel(status: UIAppointmentStatus, modifier: Modifier = Modifier) {
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
