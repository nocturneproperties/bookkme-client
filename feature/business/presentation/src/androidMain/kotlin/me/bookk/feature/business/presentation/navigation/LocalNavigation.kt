package me.bookk.feature.business.presentation.navigation

import androidx.compose.runtime.compositionLocalOf

class BusinessNavigation(
    val navigateBack: () -> Unit,
)

internal val LocalNavigation = compositionLocalOf {
    BusinessNavigation(
        navigateBack = {},
    )
}