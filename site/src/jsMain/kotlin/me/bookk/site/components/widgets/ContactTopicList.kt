package me.bookk.site.components.widgets

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.flexWrap
import com.varabyte.kobweb.compose.ui.modifiers.fontWeight
import com.varabyte.kobweb.compose.ui.modifiers.gap
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.silk.style.toModifier
import dev.icerock.moko.resources.desc.desc
import me.bookk.site.content.ContactChannel
import me.bookk.site.designsystem.components.Heading
import me.bookk.site.designsystem.components.HeadingLevel
import me.bookk.site.designsystem.components.TextBlock
import me.bookk.site.designsystem.components.TextList
import me.bookk.site.designsystem.theme.BodyMediumTextStyle
import me.bookk.site.designsystem.theme.TitleMediumTextStyle
import me.bookk.site.designsystem.theme.active
import me.bookk.site.designsystem.theme.secondary
import org.jetbrains.compose.web.css.FlexWrap
import org.jetbrains.compose.web.css.cssRem

@Composable
fun ContactTopicList(channel: ContactChannel) {
    Column(Modifier.fillMaxWidth()) {
        ContactTopicHeader(channel)
        TextList(channel.topics)
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
            TextBlock(
                channel.email.desc(),
                BodyMediumTextStyle
                    .toModifier()
                    .fontWeight(FontWeight.SemiBold)
                    .active(),
            )
        }
        TextBlock(
            channel.description,
            BodyMediumTextStyle
                .toModifier()
                .secondary(),
        )
    }
}
