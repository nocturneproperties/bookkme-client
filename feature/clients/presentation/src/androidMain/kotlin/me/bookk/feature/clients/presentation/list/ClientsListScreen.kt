package me.bookk.feature.clients.presentation.list

import androidx.compose.foundation.layout.Arrangement
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
        section.items.forEach {
            TextButton(
                modifier = Modifier
                    .height(48.dp)
                    .fillMaxWidth(),
                onClick = { section.onItemClick(it) },
                shape = RectangleShape
            ) {
                Text(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth(),
                    text = it.fullName,
                    style = MaterialTheme.typography.bodyLarge.primary(),
                    textAlign = TextAlign.Start
                )
            }
            HorizontalDivider(color = LocalColors.current.divider.copy(alpha = 0.5f))
        }
    }
}