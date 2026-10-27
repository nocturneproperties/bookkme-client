package me.bookk.designsystem.theme

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalInspectionMode
import me.bookk.designsystem.theme.color.LocalColors
import me.bookk.designsystem.theme.shapes.AppShapes


@Composable
fun AppTheme(
    themeMode: ThemeMode = ThemeMode.DARK,
    content: @Composable () -> Unit
) {
    if (!LocalInspectionMode.current) {
        val context = LocalActivity.current as ComponentActivity

        LaunchedEffect(themeMode) {
            context.enableEdgeToEdge(
                statusBarStyle = themeMode.systemBarStyle,
                navigationBarStyle = themeMode.systemBarStyle
            )
        }
    }
    CompositionLocalProvider(
        LocalColors provides themeMode.scheme.animated(),
    ) {
        MaterialTheme(
            colorScheme = themeMode.materialScheme.animated(),
            shapes = AppShapes
        ) {
            content()
        }
    }
}
