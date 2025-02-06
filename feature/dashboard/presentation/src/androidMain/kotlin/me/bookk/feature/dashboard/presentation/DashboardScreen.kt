package me.bookk.feature.dashboard.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CalendarViewDay
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import dev.icerock.moko.resources.compose.localized
import me.bookk.designsystem.theme.AppTheme
import me.bookk.designsystem.theme.ThemeMode
import me.bookk.designsystem.theme.color.LocalColors
import me.bookk.feature.dashboard.presentation.state.AndroidDashboardState
import me.bookk.feature.dashboard.presentation.state.DashboardEventListener
import me.bookk.feature.dashboard.presentation.state.DashboardState
import me.bookk.feature.dashboard.presentation.state.TabItem

@Composable
internal fun DashboardScreen(
    state: DashboardState,
    showPage: @Composable (TabItem.Id) -> Unit,
    listener: DashboardEventListener
) {
    Scaffold(
        modifier = Modifier
            .systemBarsPadding()
            .imePadding(),
        content = {
            Box(Modifier.padding(it)) {
                showPage(state.tabItems.selectedItemId)
            }
        },
        bottomBar = {
            Column {
                HorizontalDivider(color = LocalColors.current.Divider)
                NavigationBar(
                    modifier = Modifier,
                    containerColor = LocalColors.current.Background
                ) {
                    state.tabItems.items.forEach { item ->
                        NavigationBarItem(
                            selected = item.id == state.tabItems.selectedItemId,
                            label = { Text(item.text.localized()) },
                            icon = { Icon(item.id.asIcon(), contentDescription = null) },
                            onClick = { state.tabItems.selectedItemId = item.id },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = Color.Transparent,
                                selectedIconColor = LocalColors.current.ActionText,
                                unselectedIconColor = LocalColors.current.Inactive,
                                selectedTextColor = LocalColors.current.ActionText,
                                unselectedTextColor = LocalColors.current.Inactive
                            )
                        )
                    }
                }
            }
        }
    )
}

private fun TabItem.Id.asIcon():ImageVector {
    return when(this) {
        TabItem.Id.APPOINTMENTS -> Icons.Filled.CalendarViewDay
        TabItem.Id.BUSINESS -> Icons.Filled.Business
        TabItem.Id.SETTINGS -> Icons.Filled.Settings
    }
}

@Preview
@Composable
private fun PreviewDark() {
    AppTheme(themeMode = ThemeMode.DARK) {
        DashboardScreen(
            state = AndroidDashboardState(DashboardViewModel.createInitData()),
            showPage = {},
            listener = mockListener()
        )
    }
}

@Preview
@Composable
private fun PreviewLight() {
    AppTheme(themeMode = ThemeMode.LIGHT) {
        DashboardScreen(
            state = AndroidDashboardState(DashboardViewModel.createInitData()),
            showPage = {},
            listener = mockListener()
        )
    }
}

private fun mockListener() = object : DashboardEventListener {
}