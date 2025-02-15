package me.bookk.designsystem.theme.color

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import dev.icerock.moko.resources.ColorResource
import dev.icerock.moko.resources.compose.colorResource
import me.bookk.designsystem.resources.DesignSystem


object AppColors {

    val White: Color
        @Composable
        get() = colorResource(resource = DesignSystem.colors.white)
    val Black: Color
        @Composable
        get() = colorResource(resource = DesignSystem.colors.black)


    val LightScheme: AppColorScheme
        @Composable
        get() = AppColorScheme(
            background = colorResource(resource = DesignSystem.colors.lightBackground),
            primaryText = colorResource(resource = DesignSystem.colors.lightPrimaryText),
            secondaryText = colorResource(resource = DesignSystem.colors.lightSecondaryText),
            hintText = colorResource(resource = DesignSystem.colors.lightHintText),
            elevated = colorResource(resource = DesignSystem.colors.lightElevated),
            divider = colorResource(resource = DesignSystem.colors.lightDivider),
            actionText = colorResource(resource = DesignSystem.colors.lightActionText),
            actionTextDisabled = colorResource(resource = DesignSystem.colors.lightActionTextDisabled),
            inactive = colorResource(resource = DesignSystem.colors.lightInactive),
            blurredBackground = colorResource(resource = DesignSystem.colors.lightBlurredBackground),
            header = colorResource(resource = DesignSystem.colors.lightHeader),
            success = colorResource(resource = DesignSystem.colors.lightSuccess),
            inactiveToggle = colorResource(resource = DesignSystem.colors.lightInactiveToggle),
            buttonPrimary = colorResource(resource = DesignSystem.colors.lightButton),
            buttonActive = colorResource(resource = DesignSystem.colors.lightButtonActive),
            buttonInactive = colorResource(resource = DesignSystem.colors.lightButtonInactive),
            error = colorResource(resource = DesignSystem.colors.error)
        )

    val DarkScheme: AppColorScheme
        @Composable
        get() = AppColorScheme(
            background = colorResource(resource = DesignSystem.colors.darkBackground),
            primaryText = colorResource(resource = DesignSystem.colors.darkPrimaryText),
            secondaryText = colorResource(resource = DesignSystem.colors.darkSecondaryText),
            hintText = colorResource(resource = DesignSystem.colors.darkHintText),
            elevated = colorResource(resource = DesignSystem.colors.darkElevated),
            divider = colorResource(resource = DesignSystem.colors.darkDivider),
            actionText = colorResource(resource = DesignSystem.colors.darkActionText),
            actionTextDisabled = colorResource(resource = DesignSystem.colors.darkActionTextDisabled),
            inactive = colorResource(resource = DesignSystem.colors.darkInactive),
            blurredBackground = colorResource(resource = DesignSystem.colors.darkBlurredBackground),
            header = colorResource(resource = DesignSystem.colors.darkHeader),
            success = colorResource(resource = DesignSystem.colors.darkSuccess),
            inactiveToggle = colorResource(resource = DesignSystem.colors.darkInactiveToggle),
            buttonPrimary = colorResource(resource = DesignSystem.colors.darkButton),
            buttonActive = colorResource(resource = DesignSystem.colors.darkButtonActive),
            buttonInactive = colorResource(resource = DesignSystem.colors.darkButtonInactive),
            error = colorResource(resource = DesignSystem.colors.error)
        )
}

@Composable
fun animateResource(resource: ColorResource): Color {
    return animateColor(colorResource(resource = resource))
}

@Composable
fun animateColor(color: Color): Color {
    return animateColorAsState(color, animationSpec = spring(stiffness = Spring.StiffnessLow)).value
}