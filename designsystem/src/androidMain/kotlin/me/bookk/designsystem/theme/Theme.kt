package me.bookk.designsystem.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import me.bookk.designsystem.theme.color.LocalColors
import me.bookk.designsystem.theme.shapes.AppShapes


@Composable
fun AppTheme(
    themeMode: ThemeMode = ThemeMode.DARK,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalColors provides themeMode.scheme,
    ) {
        MaterialTheme(
            colorScheme = themeMode.materialTheme,
            shapes = AppShapes
        ) {
            content()
        }
    }
}
