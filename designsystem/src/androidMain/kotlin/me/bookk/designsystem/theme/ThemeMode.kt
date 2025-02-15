package me.bookk.designsystem.theme

import androidx.activity.SystemBarStyle
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import me.bookk.designsystem.theme.color.AppColorScheme
import me.bookk.designsystem.theme.color.AppColors
import me.bookk.designsystem.theme.color.animateColor

enum class ThemeMode {
    LIGHT,
    DARK;

    val systemBarStyle:SystemBarStyle
        get() = when(this) {
            LIGHT -> SystemBarStyle.light(Color.Transparent.toArgb(), Color.Transparent.toArgb())
            DARK -> SystemBarStyle.dark(Color.Transparent.toArgb())
        }

    val scheme: AppColorScheme
        @Composable
        get() = when (this) {
            LIGHT -> AppColors.LightScheme
            DARK -> AppColors.DarkScheme
        }

    val materialScheme: ColorScheme
        @Composable
        get() {
            val currentScheme = scheme
            return when (this) {
                LIGHT -> lightColorScheme(
                    background = currentScheme.background,
                    primary = currentScheme.primaryText,
                    secondary = AppColors.White,
                    error = currentScheme.error,
                    onPrimary = AppColors.White,
                    onSecondary = currentScheme.primaryText,
                    onBackground = currentScheme.primaryText,
                    onSurface = currentScheme.primaryText,
                    onError = AppColors.White,
                    outline = currentScheme.divider,
                    outlineVariant = currentScheme.divider
                )

                DARK -> darkColorScheme(
                    background = currentScheme.background,
                    primary = currentScheme.primaryText,
                    secondary = AppColors.Black,
                    error = currentScheme.error,
                    onPrimary = AppColors.Black,
                    onSecondary = currentScheme.primaryText,
                    onBackground = currentScheme.primaryText,
                    onSurface = currentScheme.primaryText,
                    onError = AppColors.Black,
                    outline = currentScheme.divider,
                    outlineVariant = currentScheme.divider
                )
            }
        }
}

@Composable
fun AppColorScheme.animated():AppColorScheme {
    return AppColorScheme(
        background = animateColor(background),
        primaryText = animateColor(primaryText),
        secondaryText = animateColor(secondaryText),
        hintText = animateColor(hintText),
        elevated = animateColor(elevated),
        divider = animateColor(divider),
        actionText = animateColor(actionText),
        actionTextDisabled = animateColor(actionTextDisabled),
        inactive = animateColor(inactive),
        blurredBackground = animateColor(primaryText),
        header = animateColor(header),
        success = animateColor(success),
        inactiveToggle = animateColor(primaryText),
        buttonPrimary = animateColor(buttonPrimary),
        buttonActive = animateColor(buttonActive),
        buttonInactive = animateColor(buttonInactive),
        error = animateColor(error)
    )
}

@Composable
fun ColorScheme.animated(): ColorScheme {
    return copy(
        primary = animateColor(primary),
        onPrimary = animateColor(onPrimary),
        primaryContainer = animateColor(primaryContainer),
        onPrimaryContainer = animateColor(onPrimaryContainer),
        inversePrimary = animateColor(inversePrimary),
        secondary = animateColor(secondary),
        onSecondary = animateColor(onSecondary),
        secondaryContainer = animateColor(secondaryContainer),
        onSecondaryContainer = animateColor(onSecondaryContainer),
        tertiary = animateColor(tertiary),
        onTertiary = animateColor(onTertiary),
        tertiaryContainer = animateColor(tertiaryContainer),
        onTertiaryContainer = animateColor(onTertiaryContainer),
        background = animateColor(background),
        onBackground = animateColor(onBackground),
        surface = animateColor(surface),
        onSurface = animateColor(onSurface),
        surfaceVariant = animateColor(surfaceVariant),
        onSurfaceVariant = animateColor(onSurfaceVariant),
        surfaceTint = animateColor(surfaceTint),
        inverseSurface = animateColor(inverseSurface),
        inverseOnSurface = animateColor(inverseOnSurface),
        error = animateColor(error),
        onError = animateColor(onError),
        errorContainer = animateColor(errorContainer),
        onErrorContainer = animateColor(onErrorContainer),
        outline = animateColor(outline),
        outlineVariant = animateColor(outlineVariant),
        scrim = animateColor(scrim),
        surfaceBright = animateColor(surfaceBright),
        surfaceDim = animateColor(surfaceDim),
        surfaceContainer = animateColor(surfaceContainer),
        surfaceContainerHigh = animateColor(surfaceContainerHigh),
        surfaceContainerHighest = animateColor(surfaceContainerHighest),
        surfaceContainerLow = animateColor(surfaceContainerLow),
        surfaceContainerLowest = animateColor(surfaceContainerLowest)
    )
}
