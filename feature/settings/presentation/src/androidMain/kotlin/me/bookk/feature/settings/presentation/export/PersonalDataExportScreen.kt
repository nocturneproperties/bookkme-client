package me.bookk.feature.settings.presentation.export

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import me.bookk.android.designsystem.resources.DesignSystem
import me.bookk.android.feature.settings.resources.SettingsRes
import me.bookk.designsystem.components.ActionButton
import me.bookk.designsystem.components.AppCard
import me.bookk.designsystem.components.AppTopBar
import me.bookk.designsystem.components.StateTextButton
import me.bookk.designsystem.painter
import me.bookk.designsystem.resources.color.themed
import me.bookk.designsystem.theme.color.LocalColors
import me.bookk.designsystem.theme.typography.secondary

@Composable
internal fun PersonalDataExportScreen(state: PersonalDataExportState) {
    Scaffold(topBar = { AppTopBar(state.appBar) }) { padding ->
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            ExportSummary(state)
            state.requestAvailability?.let {
                Text(
                    text = it.localized(),
                    modifier = Modifier.padding(horizontal = 12.dp),
                    style = MaterialTheme.typography.bodyMedium.secondary(),
                )
            }
            state.error?.let {
                Text(
                    text = it.localized(),
                    modifier = Modifier.padding(horizontal = 12.dp),
                    color = LocalColors.current.error,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            if (state.request.isVisible) {
                PasskeyConfirmation()
                ActionButton(state.request, modifier = Modifier.fillMaxWidth())
            }
            if (state.download.isVisible) {
                ActionButton(state.download, modifier = Modifier.fillMaxWidth())
            }
            if (state.refresh.isVisible) {
                StateTextButton(
                    state.refresh,
                    modifier = Modifier.fillMaxWidth().border(
                        BorderStroke(1.dp, LocalColors.current.actionText),
                        MaterialTheme.shapes.large,
                    ),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                    ),
                )
            }
            state.status.footer?.let {
                Text(
                    text = it.localized(),
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                    style = MaterialTheme.typography.bodyMedium.secondary(),
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Composable
private fun ExportSummary(state: PersonalDataExportState) {
    AppCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            StatusIndicator(state.status)
            Text(
                text = state.status.title.localized(),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                ),
                color = LocalColors.current.primaryText,
            )
            state.status.message?.let {
                Text(text = it.localized(), style = MaterialTheme.typography.bodyLarge.secondary())
            }
            if (state.status.showsFile) {
                ExportFileRow()
            }
            state.expiry?.let {
                HorizontalDivider(color = LocalColors.current.divider)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text(
                        text = SettingsRes.strings.settings_export_expiry_label.desc().localized(),
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Text(
                        text = it.localized(),
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.End,
                    )
                }
            }
        }
    }
}

@Composable
private fun StatusIndicator(status: UIExportStatus) {
    if (status.isInProgress) {
        CircularProgressIndicator(
            modifier = Modifier.size(40.dp),
            color = status.color.themed,
            strokeWidth = 3.dp,
        )
    } else {
        status.icon?.let {
            Icon(
                painter = it.painter(),
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = status.color.themed,
            )
        }
    }
}

@Composable
private fun ExportFileRow() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Icon(
            painter = DesignSystem.images.document.painter(),
            contentDescription = null,
            modifier = Modifier.size(32.dp),
            tint = LocalColors.current.secondaryText,
        )
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = SettingsRes.strings.settings_export_file_title.desc().localized(),
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
            )
            Text(
                text = SettingsRes.strings.settings_export_file_type.desc().localized(),
                style = MaterialTheme.typography.bodyMedium.secondary(),
            )
        }
    }
}

@Composable
private fun PasskeyConfirmation() {
    AppCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(
                painter = DesignSystem.images.key.painter(),
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = LocalColors.current.secondaryText,
            )
            Text(
                text = SettingsRes.strings.settings_export_confirmation.desc().localized(),
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}
