package me.bookk.site.components.widgets

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.AlignItems
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.alignItems
import com.varabyte.kobweb.compose.ui.modifiers.flexBasis
import com.varabyte.kobweb.compose.ui.modifiers.flexGrow
import com.varabyte.kobweb.compose.ui.modifiers.gap
import com.varabyte.kobweb.compose.ui.modifiers.id
import com.varabyte.kobweb.compose.ui.modifiers.minWidth
import com.varabyte.kobweb.compose.ui.modifiers.scrollMarginBlock
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.base
import com.varabyte.kobweb.silk.style.breakpoint.Breakpoint
import com.varabyte.kobweb.silk.style.toModifier
import dev.icerock.moko.resources.desc.desc
import me.bookk.site.content.ContactChannel
import me.bookk.site.designsystem.components.ButtonLink
import me.bookk.site.designsystem.components.ButtonLinkVariant
import me.bookk.site.designsystem.components.DividedRow
import me.bookk.site.designsystem.components.Heading
import me.bookk.site.designsystem.components.HeadingLevel
import me.bookk.site.designsystem.components.TextBlock
import me.bookk.site.designsystem.components.TextList
import me.bookk.site.designsystem.theme.BodyMediumTextStyle
import me.bookk.site.designsystem.theme.HeadlineSmallTextStyle
import me.bookk.site.designsystem.theme.LabelMediumTextStyle
import me.bookk.site.designsystem.theme.secondary
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.px

val ContactChannelSummaryStyle = CssStyle {
    base {
        Modifier
            .flexBasis(100.percent)
            .minWidth(0.px)
            .gap(0.375.cssRem)
    }
    Breakpoint.MD { Modifier.flexBasis(15.cssRem) }
}

val ContactChannelTopicsStyle = CssStyle {
    base {
        Modifier
            .flexBasis(100.percent)
            .minWidth(0.px)
    }
    Breakpoint.MD {
        Modifier
            .flexGrow(1)
            .flexBasis(0.px)
    }
}

val ContactChannelActionStyle = CssStyle {
    base {
        Modifier
            .flexBasis(100.percent)
            .gap(0.5.cssRem)
    }
    Breakpoint.MD {
        Modifier
            .flexBasis(15.cssRem)
            .alignItems(AlignItems.FlexEnd)
    }
}

@Composable
fun ContactChannelRow(channel: ContactChannel) {
    DividedRow(
        Modifier
            .id(channel.id)
            .scrollMarginBlock(start = 4.cssRem),
        verticalAlignment = Alignment.Top,
    ) {
        Column(ContactChannelSummaryStyle.toModifier()) {
            Heading(channel.title, HeadingLevel.H2, HeadlineSmallTextStyle.toModifier())
            TextBlock(
                channel.description,
                BodyMediumTextStyle
                    .toModifier()
                    .secondary(),
            )
        }
        TextList(channel.topics, ContactChannelTopicsStyle.toModifier())
        Column(ContactChannelActionStyle.toModifier()) {
            ButtonLink(channel.action, "mailto:${channel.email}", ButtonLinkVariant.TONAL)
            TextBlock(
                channel.email.desc(),
                LabelMediumTextStyle
                    .toModifier()
                    .secondary(),
            )
        }
    }
}
