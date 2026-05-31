package me.bookk.feature.services.presentation.group.list

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import me.bookk.designsystem.components.AppTopBar
import me.bookk.designsystem.components.CollapsingAppBarScaffold
import me.bookk.designsystem.components.List
import me.bookk.designsystem.components.PullToRefresh
import me.bookk.designsystem.components.TextField
import me.bookk.designsystem.resources.DesignSystem
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
    val haptics = LocalHapticFeedback.current
    var menuExpanded by remember { mutableStateOf(false) }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .combinedClickable(
                onLongClick = {
                    haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                    menuExpanded = true
                },
                onClick = { item.onItemClick() }
            )
            .height(48.dp)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(item.name)
    }
    DropdownMenu(
        expanded = menuExpanded,
        containerColor = LocalColors.current.elevated,
        onDismissRequest = { menuExpanded = false }
    ) {
        DropdownMenuItem(
            text = {
                Text(
                    DesignSystem.strings.action_delete.desc().localized(),
                    color = LocalColors.current.error
                )
            },
            onClick = { item.onDeleteClick() }
        )
    }
}
