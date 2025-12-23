package me.bookk.feature.settings.presentation.passkey

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import me.bookk.designsystem.components.AppCard
import me.bookk.designsystem.components.AppTopBar
import me.bookk.designsystem.components.ObserveNotifications
import me.bookk.designsystem.components.PullToRefresh
import me.bookk.designsystem.components.TextButton
import me.bookk.designsystem.components.TopBarSize
import me.bookk.designsystem.modifier.bottomShadow
import me.bookk.designsystem.theme.AppTheme
import me.bookk.designsystem.theme.ThemeMode
import me.bookk.designsystem.theme.color.LocalColors
import me.bookk.feature.settings.presentation.navigation.LocalNavigation
import kotlin.uuid.Uuid

@Composable
internal fun PasskeyScreen(state: PasskeyState) {
    ObserveNotifications(state = state.notification)
    Scaffold(
        topBar = {
            AppTopBar(
                state = state.appBar,
                size = TopBarSize.MEDIUM,
                onNavigationIconClick = LocalNavigation.current.navigateBack,
                actions = {
                    TextButton(
                        state = state.addPasskeyButton,
                        onClick = LocalPasskeyEventListener.current.onAddPasskeyClick
                    )
                }
            )
        },
        content = { paddings ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddings)
                    .padding(16.dp)
            ) {
                PullToRefresh(
                    modifier = Modifier.fillMaxSize(),
                    state = state.refresh,
                    onRefresh = LocalPasskeyEventListener.current.onRefresh
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 8.dp)
                            .bottomShadow(64.dp, LocalColors.current.background),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(bottom = 64.dp)
                    ) {
                        items(state.passkeys, key = { it.id }) {
                            PasskeyItem(modifier = Modifier.animateItem(), it)
                        }
                    }
                }
            }
        }
    )
}

@Composable
private fun PasskeyItem(modifier: Modifier, item: PasskeyState.PasskeyItem) {
    AppCard(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxWidth()
                .animateContentSize(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Filled.Key, contentDescription = null)
            Column(Modifier.weight(1f)) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    item.addedOn.localized(),
                    style = MaterialTheme.typography.bodySmall,
                    color = LocalColors.current.secondaryText
                )
            }
            val listener = LocalPasskeyEventListener.current
            if (item.isDeletable) {
                IconButton(onClick = { listener.onDeletePasskeyClick(item) }) {
                    Icon(
                        Icons.Outlined.DeleteOutline,
                        contentDescription = null,
                        tint = LocalColors.current.error
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun PreviewDark() {
    AppTheme(themeMode = ThemeMode.DARK) {
        PasskeyScreen(
            state = AndroidPasskeyState(PasskeyViewModel.createInitData()).apply {
                replacePasskeyList(mockPasskeys())
            }
        )
    }
}

@Preview
@Composable
private fun PreviewLight() {
    AppTheme(themeMode = ThemeMode.LIGHT) {
        PasskeyScreen(
            state = AndroidPasskeyState(PasskeyViewModel.createInitData()).apply {
                replacePasskeyList(mockPasskeys())
            }
        )
    }
}

private fun mockPasskeys() = buildList {
    repeat(10) {
        add(
            PasskeyState.PasskeyItem(
                id = Uuid.random(),
                title = "Passkey name",
                isBackedUp = false,
                isDeletable = true,
                addedOn = "Added on Mar 24, 2025".desc()
            )
        )
    }
}