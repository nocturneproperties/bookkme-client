package me.bookk.designsystem.theme.color

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

interface ColorSchemeProvider {
    val Background: Color
        @Composable
        get
    val PrimaryText: Color
        @Composable
        get
    val HintText: Color
        @Composable
        get
    val Elevated: Color
        @Composable
        get
    val Divider: Color
        @Composable
        get
    val ActionText: Color
        @Composable
        get
    val ActionTextDisabled: Color
        @Composable
        get
    val Inactive: Color
        @Composable
        get
    val BlurredBackground: Color
        @Composable
        get
    val Header: Color
        @Composable
        get
    val Success: Color
        @Composable
        get
    val InactiveToggle: Color
        @Composable
        get
    val ButtonPrimary: Color
        @Composable
        get
    val ButtonInactive: Color
        @Composable
        get

    @Composable
    fun toColorScheme(): ColorScheme
}

val LocalColors = staticCompositionLocalOf<ColorSchemeProvider> {
    AppColors.Dark
}