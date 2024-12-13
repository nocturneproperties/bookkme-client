package me.bookk.designsystem.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import me.bookk.designsystem.theme.color.LocalColors
import me.bookk.designsystem.theme.shapes.AppShapes
import me.bookk.designsystem.theme.text.AppMaterialTypography
import me.bookk.designsystem.theme.text.AppTypography
import me.bookk.designsystem.theme.text.LocalAppTypography


@Composable
fun AppTheme(
    themeMode: ThemeMode = ThemeMode.DARK,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalAppTypography provides AppTypography,
        LocalColors provides themeMode.scheme,
    ) {
        MaterialTheme(
            colorScheme = themeMode.materialTheme,
            typography = AppMaterialTypography,
            shapes = AppShapes
        ) {
            content()
        }
    }
}
