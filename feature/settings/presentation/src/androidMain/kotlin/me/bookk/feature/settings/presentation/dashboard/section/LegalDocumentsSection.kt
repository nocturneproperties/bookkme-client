package me.bookk.feature.settings.presentation.dashboard.section

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import me.bookk.android.feature.settings.resources.SettingsRes
import me.bookk.designsystem.components.ActionButton
import me.bookk.designsystem.components.StateTextButton
import me.bookk.designsystem.resources.color.themed
import me.bookk.designsystem.theme.color.LocalColors
import me.bookk.feature.settings.presentation.dashboard.LegalDocumentItem
import me.bookk.feature.settings.presentation.dashboard.LegalDocumentsSection

@Composable
internal fun LegalDocumentsSection(state: LegalDocumentsSection) {
    val loadingLabel = SettingsRes.strings.settings_legal_loading.desc().localized()
    Column(
        modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = state.title.localized(),
                style = MaterialTheme.typography.titleMedium,
                color = LocalColors.current.header,
            )
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp).semantics {
                        contentDescription = loadingLabel
                    },
                    strokeWidth = 2.dp,
                    color = LocalColors.current.secondaryText,
                )
            }
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.large)
                .background(LocalColors.current.elevated),
        ) {
            state.documents.forEachIndexed { index, document ->
                key(document.id) {
                    if (index > 0) {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 24.dp),
                            color = LocalColors.current.divider,
                        )
                    }
                    LegalDocumentRow(document)
                }
            }
            if (state.documents.isEmpty()) {
                if (state.isLoading) {
                    LegalDocumentsMessage(loadingLabel)
                } else {
                    state.message?.let { LegalDocumentsMessage(it.localized()) }
                }
            }
            if (state.retry.isVisible) {
                StateTextButton(
                    state.retry,
                    modifier = Modifier.padding(start = 8.dp, bottom = 8.dp),
                )
            }
        }
        if (state.documents.isNotEmpty()) {
            state.message?.let {
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                    text = it.localized(),
                    style = MaterialTheme.typography.bodySmall,
                    color = LocalColors.current.secondaryText,
                )
            }
        }
        if (state.accept.isVisible) {
            ActionButton(state.accept, modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
private fun LegalDocumentRow(document: LegalDocumentItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(role = Role.Button, onClick = document.onClick)
            .heightIn(min = 64.dp)
            .padding(horizontal = 24.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = document.title.localized(),
                style = MaterialTheme.typography.titleMedium,
                color = LocalColors.current.primaryText,
            )
            Text(
                text = document.status.localized(),
                style = MaterialTheme.typography.bodySmall,
                color = document.statusColor.themed,
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.OpenInNew,
            contentDescription = null,
            tint = LocalColors.current.secondaryText,
            modifier = Modifier.size(18.dp),
        )
    }
}

@Composable
private fun LegalDocumentsMessage(text: String) {
    Text(
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp),
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = LocalColors.current.secondaryText,
    )
}
