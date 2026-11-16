package me.bookk.site.designsystem.components

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.color
import com.varabyte.kobweb.silk.style.toModifier
import dev.icerock.moko.resources.desc.StringDesc
import me.bookk.designsystem.resources.color.ColorToken
import me.bookk.designsystem.uistate.ValidationState
import me.bookk.site.designsystem.theme.BodySmallTextStyle
import me.bookk.site.designsystem.theme.themed

@Composable
fun SupportingText(text: StringDesc?, validationState: ValidationState) {
    text ?: return
    TextBlock(
        text,
        BodySmallTextStyle
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
