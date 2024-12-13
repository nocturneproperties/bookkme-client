package me.bookk.designsystem.theme.color

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import dev.icerock.moko.resources.compose.colorResource
import me.bookk.designsystem.resources.DesignSystem


object AppColors {

    val White: Color
        @Composable
        get() = colorResource(resource = DesignSystem.colors.white)
    val Black: Color
        @Composable
        get() = colorResource(resource = DesignSystem.colors.black)
    val Error: Color
        @Composable
        get() = colorResource(resource = DesignSystem.colors.error)

    object Light : ColorSchemeProvider {
        override val Background: Color
            @Composable
            get() = colorResource(resource = DesignSystem.colors.lightBackground)
        override val PrimaryText: Color
            @Composable
            get() = colorResource(resource = DesignSystem.colors.lightPrimaryText)
        override val HintText: Color
            @Composable
            get() = colorResource(resource = DesignSystem.colors.lightHintText)
        override val Elevated: Color
            @Composable
            get() = colorResource(resource = DesignSystem.colors.lightElevated)
        override val Divider: Color
            @Composable
            get() = colorResource(resource = DesignSystem.colors.lightDivider)
        override val ActionText: Color
            @Composable
            get() = colorResource(resource = DesignSystem.colors.lightActionText)
        override val ActionTextDisabled: Color
            @Composable
            get() = colorResource(resource = DesignSystem.colors.lightActionTextDisabled)
        override val Inactive: Color
            @Composable
            get() = colorResource(resource = DesignSystem.colors.lightInactive)
        override val BlurredBackground: Color
            @Composable
            get() = colorResource(resource = DesignSystem.colors.lightBlurredBackground)
        override val Header: Color
            @Composable
            get() = colorResource(resource = DesignSystem.colors.lightHeader)
        override val Success: Color
            @Composable
            get() = colorResource(resource = DesignSystem.colors.lightSuccess)
        override val InactiveToggle: Color
            @Composable
            get() = colorResource(resource = DesignSystem.colors.lightInactiveToggle)
        override val ButtonPrimary: Color
            @Composable
            get() = colorResource(resource = DesignSystem.colors.lightButton)
        override val ButtonInactive: Color
            @Composable
            get() = colorResource(resource = DesignSystem.colors.lightButtonInactive)

        @Composable
        override fun toColorScheme(): ColorScheme {
            return lightColorScheme(
                background = Background,
                primary = PrimaryText,
                secondary = White,
                error = Error,
                onPrimary = White,
                onSecondary = PrimaryText,
                onBackground = PrimaryText,
                onSurface = PrimaryText,
                onError = White,
                outline = Divider
            )
        }
    }

    object Dark : ColorSchemeProvider {
        override val Background: Color
            @Composable
            get() = colorResource(resource = DesignSystem.colors.darkBackground)
        override val PrimaryText: Color
            @Composable
            get() = colorResource(resource = DesignSystem.colors.darkPrimaryText)
        override val HintText: Color
            @Composable
            get() = colorResource(resource = DesignSystem.colors.darkHintText)
        override val Elevated: Color
            @Composable
            get() = colorResource(resource = DesignSystem.colors.darkElevated)
        override val Divider: Color
            @Composable
            get() = colorResource(resource = DesignSystem.colors.darkDivider)
        override val ActionText: Color
            @Composable
            get() = colorResource(resource = DesignSystem.colors.darkActionText)
        override val ActionTextDisabled: Color
            @Composable
            get() = colorResource(resource = DesignSystem.colors.darkActionTextDisabled)
        override val Inactive: Color
            @Composable
            get() = colorResource(resource = DesignSystem.colors.darkInactive)
        override val BlurredBackground: Color
            @Composable
            get() = colorResource(resource = DesignSystem.colors.darkBlurredBackground)
        override val Header: Color
            @Composable
            get() = colorResource(resource = DesignSystem.colors.darkHeader)
        override val Success: Color
            @Composable
            get() = colorResource(resource = DesignSystem.colors.darkSuccess)
        override val InactiveToggle: Color
            @Composable
            get() = colorResource(resource = DesignSystem.colors.darkInactiveToggle)
        override val ButtonPrimary: Color
            @Composable
            get() = colorResource(resource = DesignSystem.colors.darkButton)
        override val ButtonInactive: Color
            @Composable
            get() = colorResource(resource = DesignSystem.colors.darkButtonInactive)

        @Composable
        override fun toColorScheme(): ColorScheme {
            return darkColorScheme(
                background = Background,
                primary = PrimaryText,
                secondary = Black,
                error = Error,
                onPrimary = Black,
                onSecondary = PrimaryText,
                onBackground = PrimaryText,
                onSurface = PrimaryText,
                onError = Black,
                outline = Divider
            )
        }
    }
}