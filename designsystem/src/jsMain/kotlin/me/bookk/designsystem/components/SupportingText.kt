package me.bookk.designsystem.components

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.ui.modifiers.color
import com.varabyte.kobweb.silk.style.toModifier
import dev.icerock.moko.resources.desc.StringDesc
import me.bookk.designsystem.resources.color.ColorToken
import me.bookk.designsystem.theme.BodyMediumTextStyle
import me.bookk.designsystem.theme.themed
import me.bookk.designsystem.uistate.ValidationState

@Composable
fun SupportingText(text: StringDesc?, validationState: ValidationState) {
    text ?: return
    TextBlock(
        text,
        BodyMediumTextStyle
            .toModifier()
            .color(validationState.colorToken().themed),
    )
}

private fun ValidationState.colorToken(): ColorToken {
    return when (this) {
        ValidationState.ERROR -> ColorToken.Error
        ValidationState.WARNING -> ColorToken.SecondaryText
        ValidationState.DEFAULT -> ColorToken.SecondaryText
    }
}
