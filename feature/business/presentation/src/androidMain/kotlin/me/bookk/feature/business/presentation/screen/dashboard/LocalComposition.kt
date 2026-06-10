package me.bookk.feature.business.presentation.screen.dashboard

import androidx.compose.runtime.compositionLocalOf
import me.bookk.feature.business.presentation.screen.dashboard.state.DashboardUIItem

internal class DashboardEventListener(
    val onItemClicked: (DashboardUIItem) -> Unit,
)

internal val LocalDashboardEventListener = compositionLocalOf {
    DashboardEventListener(
        onItemClicked = {}
    )
}