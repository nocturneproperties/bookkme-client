package me.bookk.feature.settings.presentation.privacy

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.localized
import me.bookk.designsystem.components.AppTopBar
import me.bookk.designsystem.components.StateSwitch
import me.bookk.designsystem.theme.color.LocalColors

@Composable
internal fun PrivacySettingsScreen(state: PrivacySettingsState) {
    Scaffold(topBar = { AppTopBar(state.appBar) }) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(top = 8.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            DiagnosticsSection(state)
            LegalDocumentsSection(state.legal)
        }
    }
}

@Composable
private fun DiagnosticsSection(state: PrivacySettingsState) {
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 16.dp),
            text = state.diagnosticsTitle.localized(),
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
