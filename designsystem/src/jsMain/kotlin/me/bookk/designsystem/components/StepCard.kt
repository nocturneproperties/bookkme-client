package me.bookk.designsystem.components

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.ColumnScope
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.backgroundColor
import com.varabyte.kobweb.compose.ui.modifiers.borderRadius
import com.varabyte.kobweb.compose.ui.modifiers.flexGrow
import com.varabyte.kobweb.compose.ui.modifiers.fontWeight
import com.varabyte.kobweb.compose.ui.modifiers.gap
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.base
import com.varabyte.kobweb.silk.style.toModifier
import dev.icerock.moko.resources.desc.StringDesc
import me.bookk.designsystem.theme.BodyLargeTextStyle
import me.bookk.designsystem.theme.HeadlineSmallTextStyle
import me.bookk.designsystem.theme.LabelMediumTextStyle
import me.bookk.designsystem.theme.SurfaceCardColor
import me.bookk.designsystem.theme.active
import me.bookk.designsystem.theme.secondary
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text

val StepCardStyle = CssStyle.base {
    Modifier
        .gap(0.75.cssRem)
        .padding(1.75.cssRem)
        .borderRadius(1.25.cssRem)
        .backgroundColor(SurfaceCardColor.value())
}

@Composable
fun StepCard(
    number: Int,
    title: StringDesc,
    description: StringDesc,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit = {},
) {
    Column(
        StepCardStyle
            .toModifier()
            .then(modifier),
    ) {
        Span(
            LabelMediumTextStyle
                .toModifier()
                .fontWeight(FontWeight.Bold)
                .active()
                .toAttrs(),
        ) {
            Text(number.toString().padStart(2, '0'))
        }
        Heading(title, HeadingLevel.H3, HeadlineSmallTextStyle.toModifier())
        TextBlock(
            description,
            BodyLargeTextStyle
                .toModifier()
                .secondary(),
        )
        Box(Modifier.flexGrow(1))
        content()
    }
}
