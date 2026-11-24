package me.bookk.designsystem.components

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.ColumnScope
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.backgroundColor
import com.varabyte.kobweb.compose.ui.modifiers.borderRadius
import com.varabyte.kobweb.compose.ui.modifiers.gap
import com.varabyte.kobweb.compose.ui.modifiers.height
import com.varabyte.kobweb.compose.ui.modifiers.margin
import com.varabyte.kobweb.compose.ui.modifiers.width
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.base
import com.varabyte.kobweb.silk.style.toModifier
import dev.icerock.moko.resources.desc.StringDesc
import me.bookk.designsystem.resources.color.ColorToken
import me.bookk.designsystem.theme.TitleMediumTextStyle
import me.bookk.designsystem.theme.resolve
import me.bookk.designsystem.theme.webColors
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.px

val AccentTitledBlockStyle = CssStyle.base {
    Modifier.gap(0.5.cssRem)
}

val AccentRuleStyle = CssStyle.base {
    Modifier
        .width(2.5.cssRem)
        .height(3.px)
        .borderRadius(2.px)
        .backgroundColor(ColorToken.ActionText.resolve(colorMode.webColors()))
        .margin(bottom = 0.5.cssRem)
}

@Composable
fun AccentTitledBlock(
    title: StringDesc,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        AccentTitledBlockStyle
            .toModifier()
            .then(modifier),
    ) {
        Box(AccentRuleStyle.toModifier())
        Heading(title, HeadingLevel.H3, TitleMediumTextStyle.toModifier())
        content()
    }
}
