package me.bookk.site.components.widgets

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.css.TextDecorationLine
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.alignItems
import com.varabyte.kobweb.compose.ui.modifiers.borderTop
import com.varabyte.kobweb.compose.ui.modifiers.color
import com.varabyte.kobweb.compose.ui.modifiers.content
import com.varabyte.kobweb.compose.ui.modifiers.display
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.flexWrap
import com.varabyte.kobweb.compose.ui.modifiers.fontWeight
import com.varabyte.kobweb.compose.ui.modifiers.gap
import com.varabyte.kobweb.compose.ui.modifiers.justifyContent
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.compose.ui.modifiers.textDecorationLine
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.selectors.after
import com.varabyte.kobweb.silk.style.toModifier
import dev.icerock.moko.resources.desc.desc
import me.bookk.designsystem.resources.color.ColorToken
import me.bookk.site.content.ContactChannel
import me.bookk.site.content.ContactTopic
import me.bookk.site.content.MailtoLink
import me.bookk.site.designsystem.components.Heading
import me.bookk.site.designsystem.components.HeadingLevel
import me.bookk.site.designsystem.components.TextBlock
import me.bookk.site.designsystem.components.TextLink
import me.bookk.site.designsystem.resources.localized
import me.bookk.site.designsystem.theme.BodyMediumTextStyle
import me.bookk.site.designsystem.theme.TitleMediumTextStyle
import me.bookk.site.designsystem.theme.resolve
import me.bookk.site.designsystem.theme.themed
import me.bookk.site.designsystem.theme.webColors
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.css.DisplayStyle
import org.jetbrains.compose.web.css.FlexWrap
import org.jetbrains.compose.web.css.JustifyContent
import org.jetbrains.compose.web.css.LineStyle
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.px

val ContactTopicRowStyle = CssStyle {
    base {
        Modifier
            .display(DisplayStyle.Flex)
            .fillMaxWidth()
            .justifyContent(JustifyContent.SpaceBetween)
            .alignItems(AlignItems.Center)
            .gap(1.cssRem)
            .padding(topBottom = 0.875.cssRem)
            .textDecorationLine(TextDecorationLine.None)
            .borderTop(1.px, LineStyle.Solid, ColorToken.Divider.resolve(colorMode.webColors()))
    }
    after {
        Modifier
            .content("›")
            .fontWeight(FontWeight.SemiBold)
            .color(ColorToken.SecondaryText.resolve(colorMode.webColors()))
    }
}

@Composable
fun ContactTopicList(channel: ContactChannel) {
    Column(Modifier.fillMaxWidth()) {
        ContactTopicHeader(channel)
        channel.topics.forEach { ContactTopicRow(channel, it) }
    }
}

@Composable
private fun ContactTopicHeader(channel: ContactChannel) {
    Column(
        Modifier
            .gap(0.25.cssRem)
            .padding(bottom = 0.75.cssRem),
    ) {
        Row(
            Modifier
                .gap(0.5.cssRem)
                .flexWrap(FlexWrap.Wrap),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Heading(channel.title, HeadingLevel.H3, TitleMediumTextStyle.toModifier())
            TextLink(
                channel.email.desc(),
                channel.mailto,
                BodyMediumTextStyle
                    .toModifier()
                    .color(ColorToken.ActionText.themed),
            )
        }
        TextBlock(
            channel.description,
            BodyMediumTextStyle
                .toModifier()
                .color(ColorToken.SecondaryText.themed),
        )
    }
}

@Composable
private fun ContactTopicRow(channel: ContactChannel, topic: ContactTopic) {
    TextLink(
        topic.label,
        MailtoLink.build(channel.email, topic.subject.localized()),
        ContactTopicRowStyle
            .toModifier()
            .then(BodyMediumTextStyle.toModifier()),
    )
}
