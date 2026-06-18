package me.bookk.designsystem.resources.color

import androidx.compose.runtime.Composable
import me.bookk.designsystem.theme.color.LocalColors

val ColorToken.themed
    @Composable
    get() = when (this) {
        ColorToken.Background -> LocalColors.current.background
        ColorToken.PrimaryText -> LocalColors.current.primaryText
        ColorToken.SecondaryText -> LocalColors.current.secondaryText
        ColorToken.HintText -> LocalColors.current.hintText
        ColorToken.Elevated -> LocalColors.current.elevated
        ColorToken.Divider -> LocalColors.current.divider
        ColorToken.ActionTextDisabled -> LocalColors.current.actionTextDisabled
        ColorToken.ActionText -> LocalColors.current.actionText
        ColorToken.Inactive -> LocalColors.current.inactive
        ColorToken.BlurredBackground -> LocalColors.current.blurredBackground
        ColorToken.Header -> LocalColors.current.header
        ColorToken.Success -> LocalColors.current.success
        ColorToken.Error -> LocalColors.current.error
        ColorToken.InactiveToggle -> LocalColors.current.inactiveToggle
        ColorToken.ButtonActive -> LocalColors.current.buttonActive
        ColorToken.ButtonInactive -> LocalColors.current.buttonInactive
        ColorToken.Button -> LocalColors.current.buttonPrimary
    }