package me.bookk.designsystem.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import me.bookk.designsystem.theme.color.AppColors
import me.bookk.designsystem.theme.color.ColorSchemeProvider

enum class ThemeMode {
    LIGHT,
    DARK;

    val scheme: ColorSchemeProvider
        get() = when (this) {
            LIGHT -> AppColors.Light
            DARK -> AppColors.Dark
        }
    val materialTheme: ColorScheme
        @Composable
        get() = when (this) {
            LIGHT -> AppColors.Light.toColorScheme()
            DARK -> AppColors.Dark.toColorScheme()
        }
}