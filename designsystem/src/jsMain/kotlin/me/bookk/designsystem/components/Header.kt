package me.bookk.designsystem.components

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.color
import com.varabyte.kobweb.silk.style.toModifier
import dev.icerock.moko.resources.desc.StringDesc
import me.bookk.designsystem.resources.color.ColorToken
import me.bookk.designsystem.theme.TitleMediumTextStyle
import me.bookk.designsystem.theme.themed

@Composable
fun Header(text: StringDesc, modifier: Modifier = Modifier) {
    Heading(
        text,
        HeadingLevel.H3,
        TitleMediumTextStyle
            .toModifier()
            .color(ColorToken.Header.themed)
            .then(modifier),
    )
}
