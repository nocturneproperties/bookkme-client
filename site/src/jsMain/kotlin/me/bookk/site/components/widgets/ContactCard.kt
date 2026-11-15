package me.bookk.site.components.widgets

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.borderTop
import com.varabyte.kobweb.compose.ui.modifiers.color
import com.varabyte.kobweb.compose.ui.modifiers.gap
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.base
import com.varabyte.kobweb.silk.style.toModifier
import dev.icerock.moko.resources.desc.desc
import me.bookk.designsystem.resources.color.ColorToken
import me.bookk.site.content.ContactChannel
import me.bookk.site.designsystem.components.AppCard
import me.bookk.site.designsystem.components.Heading
import me.bookk.site.designsystem.components.HeadingLevel
import me.bookk.site.designsystem.components.TextBlock
import me.bookk.site.designsystem.components.TextLink
import me.bookk.site.designsystem.theme.BodyLargeTextStyle
import me.bookk.site.designsystem.theme.BodyMediumTextStyle
import me.bookk.site.designsystem.theme.TitleMediumTextStyle
import me.bookk.site.designsystem.theme.resolve
import me.bookk.site.designsystem.theme.themed
import me.bookk.site.designsystem.theme.webColors
import org.jetbrains.compose.web.css.LineStyle
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.px

val ContactTopicsStyle = CssStyle.base {
    Modifier
        .gap(0.5.cssRem)
        .padding(top = 1.cssRem)
        .borderTop(1.px, LineStyle.Solid, ColorToken.Divider.resolve(colorMode.webColors()))
}

@Composable
fun ContactCard(channel: ContactChannel) {
    AppCard(Modifier.gap(1.cssRem)) {
        ContactHeading(channel)
        TextLink(
            channel.email.desc(),
            channel.mailto,
            BodyLargeTextStyle
                .toModifier()
                .color(ColorToken.ActionText.themed),
        )
        Column(ContactTopicsStyle.toModifier()) {
            channel.topics.forEach { TextBlock(it, BodyMediumTextStyle.toModifier()) }
        }
    }
}

@Composable
private fun ContactHeading(channel: ContactChannel) {
    Column(Modifier.gap(0.25.cssRem)) {
        Heading(channel.title, HeadingLevel.H2, TitleMediumTextStyle.toModifier())
        TextBlock(
            channel.description,
            BodyMediumTextStyle
                .toModifier()
                .color(ColorToken.SecondaryText.themed),
        )
    }
}
