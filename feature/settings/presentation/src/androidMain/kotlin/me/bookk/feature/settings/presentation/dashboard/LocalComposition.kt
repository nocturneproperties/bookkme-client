package me.bookk.feature.settings.presentation.dashboard

import androidx.compose.runtime.compositionLocalOf
import me.bookk.feature.settings.presentation.dashboard.AppearanceSection.UIColorScheme

internal class DashboardEventListener(
    val selectScheme: (UIColorScheme) -> Unit,
    val showTerms: () -> Unit,
    val showPolicy: () -> Unit,
    val performLogout: () -> Unit
)

internal val LocalDashboardEventListener = compositionLocalOf {
    DashboardEventListener(
        selectScheme = {},
        showTerms = {},
        showPolicy = {},
        performLogout = {}
    )
}