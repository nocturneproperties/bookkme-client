package me.bookk.site.designsystem.theme

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.ui.graphics.Color
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import me.bookk.designsystem.resources.color.ColorToken

fun ColorMode.webColors(): WebColors {
    return when (this) {
        ColorMode.LIGHT -> WebColors.light
        ColorMode.DARK -> WebColors.dark
    }
}

val ColorToken.themed: Color
    @Composable
    get() = resolve(ColorMode.current.webColors())

fun ColorToken.resolve(colors: WebColors): Color {
    return when (this) {
        ColorToken.Background -> colors.background
        ColorToken.PrimaryText -> colors.primaryText
        ColorToken.SecondaryText -> colors.secondaryText
        ColorToken.HintText -> colors.hintText
        ColorToken.Elevated -> colors.elevated
        ColorToken.Divider -> colors.divider
        ColorToken.ActionTextDisabled -> colors.actionTextDisabled
        ColorToken.ActionText -> colors.actionText
        ColorToken.Inactive -> colors.inactive
        ColorToken.BlurredBackground -> colors.blurredBackground
        ColorToken.Header -> colors.header
        ColorToken.Success -> colors.success
        ColorToken.Error -> colors.error
        ColorToken.InactiveToggle -> colors.inactiveToggle
        ColorToken.ButtonActive -> colors.buttonActive
        ColorToken.ButtonInactive -> colors.buttonInactive
        ColorToken.Button -> colors.button
        ColorToken.OnAction -> colors.onAction
    }
}
