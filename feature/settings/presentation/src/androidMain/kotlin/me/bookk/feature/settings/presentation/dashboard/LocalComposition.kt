package me.bookk.feature.settings.presentation.dashboard

import androidx.compose.runtime.compositionLocalOf
import me.bookk.feature.settings.presentation.dashboard.state.AppearanceSection.UIColorScheme

internal class DashboardEventListener(
    val selectScheme: (UIColorScheme) -> Unit,
    val showTerms: () -> Unit,
    val showPolicy: () -> Unit
)

internal val LocalDashboardEventListener = compositionLocalOf {
    DashboardEventListener(
        selectScheme = {},
        showTerms = {},
        showPolicy = {}
    )
}