package me.bookk.feature.services.presentation.group.list

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.bookk.designsystem.components.AppTopBar
import me.bookk.designsystem.components.CollapsingAppBarScaffold
import me.bookk.designsystem.components.List
import me.bookk.designsystem.components.PullToRefresh
import me.bookk.designsystem.components.TextField
import me.bookk.feature.services.presentation.group.list.ServiceGroupListState.ServiceGroupUI

@Composable
internal fun ServiceGroupListScreen(
    state: ServiceGroupListState
) {
    CollapsingAppBarScaffold(
        modifier = Modifier
            .systemBarsPadding()
            .imePadding(),
        topBar = {
            Column {
                AppTopBar(state = state.appBar, behavior = it)
                TextField(
                    state.search,
                    Modifier
                        .padding(horizontal = 16.dp)
                        .padding(top = 16.dp)
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            PullToRefresh(state.refreshState) {
                List(state.groups, idProvider = ServiceGroupUI::id) {
                    ServiceGroupItem(Modifier.animateItem(), it)
                }
            }
        }
    }
}

@Composable
private fun ServiceGroupItem(modifier: Modifier, item: ServiceGroupUI) {
    Text(modifier = modifier, text = item.name)
}
