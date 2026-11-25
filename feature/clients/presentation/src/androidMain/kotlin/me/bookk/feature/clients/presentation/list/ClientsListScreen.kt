package me.bookk.feature.clients.presentation.list

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import library.picker.standardOptionsScreenPicker
import me.bookk.designsystem.components.AppTopBar
import me.bookk.designsystem.components.List
import me.bookk.designsystem.components.PullToRefresh
import me.bookk.designsystem.components.TextField
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
