package me.bookk.feature.clients.presentation.select

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.bookk.designsystem.components.AppTopBar
import me.bookk.designsystem.components.List
import me.bookk.designsystem.components.PullToRefresh
import me.bookk.designsystem.components.TextField
import me.bookk.feature.clients.presentation.list.ClientListItem
import me.bookk.feature.clients.presentation.list.ClientRow

@Composable
internal fun SelectClientScreen(state: SelectClientState) {
    Scaffold(
        modifier = Modifier.imePadding(),
        topBar = {
            Column {
                AppTopBar(state = state.appBar)
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(top = 16.dp),
                    state = state.searchField,
                )
            }
        },
        content = { paddings ->
            Column(
                modifier = Modifier
                    .padding(paddings)
                    .fillMaxSize(),
            ) {
                PullToRefresh(state.refreshState) {
                    List(state.clients, idProvider = ClientListItem::id) {
                        ClientRow(Modifier.animateItem(), it)
                    }
                }
            }
        },
    )
}
