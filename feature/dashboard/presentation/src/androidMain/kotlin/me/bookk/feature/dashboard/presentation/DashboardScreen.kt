package me.bookk.feature.dashboard.presentation

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.icerock.moko.resources.compose.localized
import kotlinx.serialization.Serializable
import me.bookk.designsystem.theme.AppTheme
import me.bookk.designsystem.theme.ThemeMode
import me.bookk.designsystem.theme.color.LocalColors
import me.bookk.feature.dashboard.presentation.state.AndroidDashboardState
import me.bookk.feature.dashboard.presentation.state.DashboardState
import me.bookk.feature.dashboard.presentation.state.TabItem

internal sealed class BottomNavDestination {
    @Serializable
    data object Home : BottomNavDestination()

    @Serializable
    data object Business : BottomNavDestination()

    @Serializable
    data object Settings : BottomNavDestination()
}

private fun getDestinationForId(item: TabItem.Id): BottomNavDestination {
    return when (item) {
        TabItem.Id.APPOINTMENTS -> BottomNavDestination.Home
        TabItem.Id.BUSINESS -> BottomNavDestination.Business
        TabItem.Id.SETTINGS -> BottomNavDestination.Settings
    }
}

@Composable
internal fun DashboardScreen(
    state: DashboardState,
    appointmentsScreen: @Composable () -> Unit,
    businessScreen: @Composable () -> Unit,
    settingsScreen: @Composable () -> Unit
) {
    val navController = rememberNavController()
    Scaffold(
        modifier = Modifier
            .systemBarsPadding()
            .imePadding(),
        content = {
            NavHost(
                modifier = Modifier.padding(it),
                navController = navController,
                startDestination = BottomNavDestination.Home
            ) {
                composable<BottomNavDestination.Home> {
                    appointmentsScreen()
                }
                composable<BottomNavDestination.Business> {
                    businessScreen()
                }
                composable<BottomNavDestination.Settings> {
                    settingsScreen()
                }
            }
        },
        bottomBar = {
            Column {
                HorizontalDivider(color = LocalColors.current.divider)
                NavigationBar(
                    modifier = Modifier,
                    containerColor = LocalColors.current.background
                ) {
                    state.tabItems.items.forEach { item ->
                        NavigationBarItem(
                            selected = item.id == state.tabItems.selectedItemId,
                            label = { Text(item.text.localized()) },
                            icon = { Icon(item.id.asIcon(), contentDescription = null) },
                            onClick = {
                                state.tabItems.selectedItemId = item.id
                            },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = Color.Transparent,
                                selectedIconColor = LocalColors.current.actionText,
                                unselectedIconColor = LocalColors.current.inactive,
                                selectedTextColor = LocalColors.current.actionText,
                                unselectedTextColor = LocalColors.current.inactive
                            )
                        )
                    }
                }
            }
            LaunchedEffect(state.tabItems.selectedItemId) {
                navController.navigate(getDestinationForId(state.tabItems.selectedItemId)) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        }
    )
}

private fun TabItem.Id.asIcon(): ImageVector {
    return when (this) {
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
            appointmentsScreen = {},
            businessScreen = {},
            settingsScreen = {}
        )
    }
}

@Preview
@Composable
private fun PreviewLight() {
    AppTheme(themeMode = ThemeMode.LIGHT) {
        DashboardScreen(
            state = AndroidDashboardState(DashboardViewModel.createInitData()),
            appointmentsScreen = {},
            businessScreen = {},
            settingsScreen = {}
        )
    }
}