package me.bookk.feature.services.presentation.group.list

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.bookk.designsystem.components.AppTopBar
import me.bookk.designsystem.components.CollapsingAppBarScaffold
import me.bookk.designsystem.components.List
import me.bookk.designsystem.components.PullToRefresh
import me.bookk.designsystem.components.TextField
import me.bookk.designsystem.theme.color.LocalColors
import me.bookk.feature.services.presentation.group.add.AddGroupDialog
import me.bookk.feature.services.presentation.group.list.ServiceGroupListState.ServiceGroupUI
import kotlin.uuid.Uuid

@Composable
internal fun ServiceGroupListScreen(
    state: ServiceGroupListState,
    businessId: Uuid
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
                List(
                    state.groups,
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .animateContentSize(),
                    idProvider = ServiceGroupUI::id
                ) {
                    ServiceGroupItem(Modifier.animateItem(), it)
                    HorizontalDivider(color = LocalColors.current.divider)
                }
            }
        }
        if (state.isAddGroupDialogVisible) {
            AddGroupDialog(businessId) { state.isAddGroupDialogVisible = false }
        }
    }
}

@Composable
private fun ServiceGroupItem(modifier: Modifier, item: ServiceGroupUI) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(item.name)
    }
}
