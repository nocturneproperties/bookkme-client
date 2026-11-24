package me.bookk.designsystem.theme

import com.varabyte.kobweb.compose.ui.graphics.Color
import dev.icerock.moko.resources.ColorResource
import me.bookk.android.designsystem.resources.DesignSystem

class WebColors(
    val background: Color,
    val primaryText: Color,
    val secondaryText: Color,
    val hintText: Color,
    val elevated: Color,
    val divider: Color,
    val actionTextDisabled: Color,
    val actionText: Color,
    val inactive: Color,
    val blurredBackground: Color,
    val header: Color,
    val success: Color,
    val error: Color,
    val inactiveToggle: Color,
    val button: Color,
    val buttonInactive: Color,
    val buttonActive: Color,
    val onAction: Color,
) {
    companion object {
        val light = WebColors(
            background = DesignSystem.colors.lightBackground.web(),
            primaryText = DesignSystem.colors.lightPrimaryText.web(),
            secondaryText = DesignSystem.colors.lightSecondaryText.web(),
            hintText = DesignSystem.colors.lightHintText.web(),
            elevated = DesignSystem.colors.lightElevated.web(),
            divider = DesignSystem.colors.lightDivider.web(),
            actionTextDisabled = DesignSystem.colors.lightActionTextDisabled.web(),
            actionText = DesignSystem.colors.lightActionText.web(),
            inactive = DesignSystem.colors.lightInactive.web(),
            blurredBackground = DesignSystem.colors.lightBlurredBackground.web(),
            header = DesignSystem.colors.lightHeader.web(),
            success = DesignSystem.colors.lightSuccess.web(),
            error = DesignSystem.colors.lightError.web(),
            inactiveToggle = DesignSystem.colors.lightInactiveToggle.web(),
            button = DesignSystem.colors.lightButton.web(),
            buttonInactive = DesignSystem.colors.lightButtonInactive.web(),
            buttonActive = DesignSystem.colors.lightButtonActive.web(),
            onAction = DesignSystem.colors.lightOnAction.web(),
        )

        val dark = WebColors(
            background = DesignSystem.colors.darkBackground.web(),
            primaryText = DesignSystem.colors.darkPrimaryText.web(),
            secondaryText = DesignSystem.colors.darkSecondaryText.web(),
            hintText = DesignSystem.colors.darkHintText.web(),
            elevated = DesignSystem.colors.darkElevated.web(),
            divider = DesignSystem.colors.darkDivider.web(),
            actionTextDisabled = DesignSystem.colors.darkActionTextDisabled.web(),
            actionText = DesignSystem.colors.darkActionText.web(),
            inactive = DesignSystem.colors.darkInactive.web(),
            blurredBackground = DesignSystem.colors.darkBlurredBackground.web(),
            header = DesignSystem.colors.darkHeader.web(),
            success = DesignSystem.colors.darkSuccess.web(),
            error = DesignSystem.colors.darkError.web(),
            inactiveToggle = DesignSystem.colors.darkInactiveToggle.web(),
            button = DesignSystem.colors.darkButton.web(),
            buttonInactive = DesignSystem.colors.darkButtonInactive.web(),
            buttonActive = DesignSystem.colors.darkButtonActive.web(),
            onAction = DesignSystem.colors.darkOnAction.web(),
        )
    }
}

private fun ColorResource.web(): Color {
    return Color.rgba(lightColor.red, lightColor.green, lightColor.blue, lightColor.alpha)
}
