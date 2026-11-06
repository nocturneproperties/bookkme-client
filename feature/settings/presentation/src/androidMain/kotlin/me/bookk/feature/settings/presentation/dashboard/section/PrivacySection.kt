package me.bookk.feature.settings.presentation.dashboard.section

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.localized
import me.bookk.designsystem.components.StateSwitch
import me.bookk.designsystem.theme.color.LocalColors
import me.bookk.feature.settings.presentation.dashboard.PrivacySection

@Composable
internal fun PrivacySection(state: PrivacySection) {
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = state.title.localized(),
            style = MaterialTheme.typography.titleMedium,
            color = LocalColors.current.header,
        )
        StateSwitch(state = state.crashReporting)
        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = state.crashReportingDescription.localized(),
            style = MaterialTheme.typography.bodySmall,
            color = LocalColors.current.secondaryText,
        )
    }
}
