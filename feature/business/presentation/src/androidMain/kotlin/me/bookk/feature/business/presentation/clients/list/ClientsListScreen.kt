package me.bookk.feature.business.presentation.clients.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.bookk.designsystem.components.AppTopBar
import me.bookk.designsystem.components.Header
import me.bookk.designsystem.components.List
import me.bookk.designsystem.components.TextField
import me.bookk.designsystem.theme.color.LocalColors
import me.bookk.designsystem.theme.typography.primary

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
                        .padding(top = 16.dp)
                )
            }
        },
        content = { pv ->
            Column(
                modifier = Modifier
                    .padding(pv)
                    .fillMaxSize()
            ) {
                List(state.clientsList, idProvider = ClientSection::id) {
                    ClientSectionItem(Modifier.animateItem(), it)
                }
            }
        }
    )
}

@Composable
private fun ClientSectionItem(modifier: Modifier, section: ClientSection) {
    Column(modifier.fillMaxWidth()) {
        Column(modifier = Modifier.height(54.dp), verticalArrangement = Arrangement.Bottom) {
            Header(modifier = Modifier.padding(horizontal = 16.dp), text = section.header)
            HorizontalDivider(color = LocalColors.current.divider)
        }
        Column(Modifier.padding(horizontal = 16.dp)) {
            section.items.forEach {
                Box(modifier = Modifier.height(48.dp), contentAlignment = Alignment.Center) {
                    Text(
                        text = it.fullName,
                        style = MaterialTheme.typography.bodyLarge.primary()
                    )
                }
                HorizontalDivider(color = LocalColors.current.divider.copy(alpha = 0.5f))
            }
        }
    }
}