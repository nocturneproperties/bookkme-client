package me.bookk.feature.dashboard.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Home
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
import androidx.compose.runtime.snapshotFlow
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
import me.bookk.designsystem.action.HideFromIme
import me.bookk.designsystem.theme.AppTheme
import me.bookk.designsystem.theme.ThemeMode
import me.bookk.designsystem.theme.color.LocalColors
import me.bookk.feature.dashboard.presentation.state.AndroidDashboardState
import me.bookk.feature.dashboard.presentation.state.DashboardState
import me.bookk.feature.dashboard.presentation.state.HomeContent
import me.bookk.feature.dashboard.presentation.state.TabItem
import me.bookk.feature.dashboard.presentation.state.TabItemsState

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
        TabItem.Id.HOME -> BottomNavDestination.Home
        TabItem.Id.BUSINESS -> BottomNavDestination.Business
        TabItem.Id.SETTINGS -> BottomNavDestination.Settings
    }
}

@Composable
internal fun DashboardScreen(
    state: DashboardState,
    homeScreen: @Composable () -> Unit,
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
                modifier = Modifier
                    .padding(it)
                    .fillMaxSize(),
                navController = navController,
                startDestination = BottomNavDestination.Home,
                enterTransition = {
                    scaleIn(
                        initialScale = 0.98f,
                        animationSpec = spring(stiffness = Spring.StiffnessHigh)
                    )
                },
                exitTransition = { ExitTransition.None }
            ) {
                composable<BottomNavDestination.Home> {
                    when (state.home.content) {
                        HomeContent.NoBusiness -> DashboardOnboardingScreen(state.home.onboarding)
                        HomeContent.SetupRequired -> DashboardSetupRequiredScreen(state.home.onboarding)
                        HomeContent.AwaitingSetup -> DashboardAwaitingSetupScreen(state.home.onboarding)
                        HomeContent.ActivePlugin -> homeScreen()
                        null -> Unit
                    }
                }
                composable<BottomNavDestination.Business> {
                    BackToHome(state.tabItems)
                    businessScreen()
                }
                composable<BottomNavDestination.Settings> {
                    BackToHome(state.tabItems)
                    settingsScreen()
                }
            }
            LaunchedEffect(navController, state.tabItems) {
                snapshotFlow { state.tabItems.selectedItemId }.collect { tab ->
                    navController.navigate(getDestinationForId(tab)) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            }
        },
        bottomBar = {
            HideFromIme {
                Column {
                    HorizontalDivider(color = LocalColors.current.divider)
                    NavigationBar(
                        modifier = Modifier,
                        containerColor = LocalColors.current.background
                    ) {
                        state.tabItems.items.forEach { item ->
                            NavigationBarItem(
                                selected = item.id == state.tabItems.selectedItemId,
                                enabled = item.isEnabled,
                                label = { Text(item.text.localized()) },
                                icon = { Icon(item.id.asIcon(), contentDescription = null) },
                                onClick = { state.tabItems.selectedItemId = item.id },
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
            }
        }
    )
}

@Composable
private fun BackToHome(tabItems: TabItemsState) {
    BackHandler { tabItems.selectedItemId = TabItem.Id.HOME }
}

private fun TabItem.Id.asIcon(): ImageVector {
    return when (this) {
        TabItem.Id.HOME -> Icons.Filled.Home
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
            homeScreen = {},
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
            homeScreen = {},
            businessScreen = {},
            settingsScreen = {}
        )
    }
}