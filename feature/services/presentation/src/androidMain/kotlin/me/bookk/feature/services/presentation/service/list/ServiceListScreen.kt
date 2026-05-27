package me.bookk.feature.services.presentation.service.list

import androidx.compose.foundation.background
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
import dev.icerock.moko.resources.compose.localized
import me.bookk.designsystem.components.AppTopBar
import me.bookk.designsystem.components.Header
import me.bookk.designsystem.components.List
import me.bookk.designsystem.components.PullToRefresh
import me.bookk.designsystem.components.SectionItem
import me.bookk.designsystem.components.TextField
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
                            shape = MaterialTheme.shapes.medium
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
    Column(modifier.fillMaxWidth()) {
        Column(modifier = Modifier.height(54.dp), verticalArrangement = Arrangement.Bottom) {
            Header(modifier = Modifier.padding(horizontal = 16.dp), text = group.name)
            HorizontalDivider(color = LocalColors.current.divider)
        }
        group.items.forEach {
            TextButton(
                modifier = Modifier
                    .height(48.dp)
                    .fillMaxWidth(),
                onClick = { group.onItemClick(it) },
                shape = RectangleShape
            ) {
                Text(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth(),
                    text = it.title,
                    style = MaterialTheme.typography.bodyLarge.primary(),
                    textAlign = TextAlign.Start
                )
            }
            HorizontalDivider(color = LocalColors.current.divider.copy(alpha = 0.5f))
        }
    }
}