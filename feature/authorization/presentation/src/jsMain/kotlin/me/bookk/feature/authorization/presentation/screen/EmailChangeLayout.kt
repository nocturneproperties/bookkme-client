package me.bookk.feature.authorization.presentation.screen

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.BoxSizing
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.boxSizing
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.fontWeight
import com.varabyte.kobweb.compose.ui.modifiers.gap
import com.varabyte.kobweb.compose.ui.modifiers.margin
import com.varabyte.kobweb.compose.ui.modifiers.maxWidth
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.silk.style.toModifier
import dev.icerock.moko.resources.desc.StringDesc
import me.bookk.designsystem.components.Heading
import me.bookk.designsystem.components.HeadingLevel
import me.bookk.designsystem.components.TextBlock
import me.bookk.designsystem.theme.BodyMediumTextStyle
import me.bookk.designsystem.theme.HeadlineLargeTextStyle
import me.bookk.designsystem.theme.secondary
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.px

private val EmailChangeModifier = Modifier
    .fillMaxWidth()
    .maxWidth(40.cssRem)
    .boxSizing(BoxSizing.BorderBox)
    .padding(top = 0.5.cssRem, bottom = 1.5.cssRem, leftRight = 1.cssRem)
    .gap(1.25.cssRem)

@Composable
internal fun EmailChangeLayout(
    title: StringDesc,
    message: StringDesc,
    content: @Composable () -> Unit,
) {
    Column(EmailChangeModifier) {
        Column(
            Modifier
                .fillMaxWidth()
                .gap(0.5.cssRem),
        ) {
            Heading(
                title,
                HeadingLevel.H1,
                HeadlineLargeTextStyle
                    .toModifier()
                    .fontWeight(FontWeight.Bold)
                    .margin(0.px),
            )
            TextBlock(
                message,
                BodyMediumTextStyle
                    .toModifier()
                    .margin(0.px)
                    .secondary(),
            )
        }
        content()
    }
}
