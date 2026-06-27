package me.bookk.designsystem.theme.color

import androidx.compose.runtime.Stable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Stable
data class AppColorScheme(
    val background: Color,
    val primaryText: Color,
    val secondaryText: Color,
    val hintText: Color,
    val elevated: Color,
    val divider: Color,
    val actionText: Color,
    val actionTextDisabled: Color,
    val inactive: Color,
    val blurredBackground: Color,
    val header: Color,
    val success: Color,
    val inactiveToggle: Color,
    val buttonPrimary: Color,
    val buttonActive: Color,
    val buttonInactive: Color,
    val error: Color,
    val onAction: Color
)

val LocalColors = staticCompositionLocalOf {
    AppColorScheme(
        background = Color.Unspecified,
        primaryText = Color.Unspecified,
        secondaryText = Color.Unspecified,
        hintText = Color.Unspecified,
        elevated = Color.Unspecified,
        divider = Color.Unspecified,
        actionText = Color.Unspecified,
        actionTextDisabled = Color.Unspecified,
        inactive = Color.Unspecified,
        blurredBackground = Color.Unspecified,
        header = Color.Unspecified,
        success = Color.Unspecified,
        inactiveToggle = Color.Unspecified,
        buttonPrimary = Color.Unspecified,
        buttonActive = Color.Unspecified,
        buttonInactive = Color.Unspecified,
        error = Color.Unspecified,
        onAction = Color.Unspecified
    )
}