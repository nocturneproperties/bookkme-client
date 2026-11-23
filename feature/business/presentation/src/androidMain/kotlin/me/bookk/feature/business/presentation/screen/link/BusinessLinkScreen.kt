package me.bookk.feature.business.presentation.screen.link

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import me.bookk.android.feature.business.resources.BusinessRes
import me.bookk.designsystem.components.ActionButton
import me.bookk.designsystem.components.AppTopBar
import me.bookk.designsystem.components.Header
import me.bookk.designsystem.components.InfoNote
import me.bookk.designsystem.components.PullToRefresh
import me.bookk.designsystem.components.StateTextButton
import me.bookk.designsystem.components.TextField
import me.bookk.designsystem.theme.color.LocalColors
import me.bookk.designsystem.theme.typography.secondary

@Composable
internal fun BusinessLinkScreen(state: BusinessLinkState) {
    Scaffold(topBar = { AppTopBar(state.appBar) }) { padding ->
        PullToRefresh(
            state = state.refresh,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                LinkEditor(state)
            }
        }
    }
}

@Composable
private fun LinkEditor(state: BusinessLinkState) {
    InfoNote(text = BusinessRes.strings.business_link_description.desc().localized())
    NameField(state)
    Text(
        text = state.preview.localized(),
        modifier = Modifier.padding(horizontal = 12.dp),
        color = LocalColors.current.actionText,
        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
    )
    Text(
        text = BusinessRes.strings.business_link_change_note.desc().localized(),
        modifier = Modifier.padding(horizontal = 12.dp),
        style = MaterialTheme.typography.bodyMedium.secondary(),
    )
    ActionButton(state.save, modifier = Modifier.fillMaxWidth())
}

@Composable
private fun NameField(state: BusinessLinkState) {
    Column {
        Header(BusinessRes.strings.business_link_name_label.desc().localized())
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            TextField(state = state.name, modifier = Modifier.weight(1f), singleLine = true)
            if (state.share.isVisible) {
                StateTextButton(state.share)
            }
        }
        state.loadError?.let {
            Text(
                text = it.localized(),
                modifier = Modifier.padding(start = 12.dp, top = 4.dp, end = 12.dp),
                color = LocalColors.current.error,
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}
