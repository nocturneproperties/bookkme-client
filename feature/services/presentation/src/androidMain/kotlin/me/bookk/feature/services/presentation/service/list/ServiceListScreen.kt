package me.bookk.feature.services.presentation.service.list

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import me.bookk.designsystem.components.AppTopBar
import me.bookk.designsystem.components.Header
import me.bookk.designsystem.components.List
import me.bookk.designsystem.components.PullToRefresh
import me.bookk.designsystem.components.SectionItem
import me.bookk.designsystem.components.TextField
import me.bookk.designsystem.resources.DesignSystem
import me.bookk.designsystem.theme.color.LocalColors
import me.bookk.designsystem.theme.typography.primary
import me.bookk.feature.services.presentation.service.list.ServiceListState.ServiceGroupUI

@Composable
internal fun ServiceListScreen(state: ServiceListState) {
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
                SectionItem(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .padding(horizontal = 16.dp)
                        .background(
                            LocalColors.current.elevated,
                            shape = MaterialTheme.shapes.large
                        ),
                    text = state.groupsSection.title.localized(),
                    onClick = state.groupsSection.onClick
                )
            }
        },
        content = { pv ->
            Column(
                modifier = Modifier
                    .padding(pv)
                    .fillMaxSize()
            ) {
                PullToRefresh(state.refreshState) {
                    List(state.services, idProvider = ServiceGroupUI::id) {
                        ServiceSectionItem(Modifier.animateItem(), it)
                    }
                }
            }
        }
    )
}

@Composable
private fun ServiceSectionItem(modifier: Modifier, group: ServiceGroupUI) {
    val haptics = LocalHapticFeedback.current
    Column(modifier.fillMaxWidth()) {
        Column(modifier = Modifier.height(54.dp), verticalArrangement = Arrangement.Bottom) {
            Header(modifier = Modifier.padding(horizontal = 16.dp), text = group.name)
            HorizontalDivider(color = LocalColors.current.divider)
        }
        group.items.forEach {
            var expanded by remember { mutableStateOf(false) }
            Box(
                modifier = Modifier
                    .combinedClickable(
                        onLongClick = {
                            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                            expanded = true
                        },
                        onClick = { group.onItemClick(it) }
                    )
                    .height(48.dp)
                    .fillMaxWidth(),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth(),
                    text = it.title,
                    style = MaterialTheme.typography.bodyLarge.primary(),
                    textAlign = TextAlign.Start
                )
                DropdownMenu(
                    expanded = expanded,
                    containerColor = LocalColors.current.elevated,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                DesignSystem.strings.action_delete.desc().localized(),
                                color = LocalColors.current.error
                            )
                        },
                        onClick = { group.onItemDeleteClick(it) }
                    )
                }
            }
            HorizontalDivider(color = LocalColors.current.divider.copy(alpha = 0.5f))
        }
    }
}