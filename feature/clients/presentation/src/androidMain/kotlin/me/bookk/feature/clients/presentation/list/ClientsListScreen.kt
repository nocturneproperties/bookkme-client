package me.bookk.feature.clients.presentation.list

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import library.picker.standardOptionsScreenPicker
import me.bookk.designsystem.components.AppTopBar
import me.bookk.designsystem.components.List
import me.bookk.designsystem.components.PullToRefresh
import me.bookk.designsystem.components.TextField
import me.bookk.designsystem.theme.color.LocalColors
import me.bookk.designsystem.theme.typography.primary
import me.bookk.designsystem.uistate.SimplePickerPresentation
import me.bookk.feature.clients.domain.api.entity.ClientImportEntry

@Composable
fun ClientsListScreen(state: ClientsListState) {
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
                        .padding(top = 16.dp),
                )
            }
        },
        content = { pv ->
            Column(
                modifier = Modifier
                    .padding(pv)
                    .fillMaxSize(),
            ) {
                PullToRefresh(state.refreshState) {
                    List(state.clientsList, idProvider = ClientListItem::id) {
                        ClientRow(Modifier.animateItem(), it)
                    }
                }
            }
        },
    )
    if (state.isContactsPickerVisible) {
        ContactsPicker(state)
    }
}

@Composable
private fun ContactsPicker(state: ClientsListState) {
    standardOptionsScreenPicker<SimplePickerPresentation<ClientImportEntry>>()(
        state.contactsPicker,
        { state.isContactsPickerVisible = false },
        state.contactsPicker.onItemsPicked,
    )
}

@Composable
private fun ClientRow(modifier: Modifier, item: ClientListItem) {
    Column(modifier.fillMaxWidth()) {
        TextButton(
            modifier = Modifier
                .height(48.dp)
                .fillMaxWidth(),
            onClick = item.onClick,
            shape = RectangleShape,
        ) {
            Text(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                text = item.fullName,
                style = MaterialTheme.typography.bodyLarge.primary(),
                textAlign = TextAlign.Start,
            )
        }
        HorizontalDivider(color = LocalColors.current.divider.copy(alpha = 0.5f))
    }
}
