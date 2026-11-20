package me.bookk.site.telegram

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.TextAlign
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.gap
import com.varabyte.kobweb.compose.ui.modifiers.height
import com.varabyte.kobweb.compose.ui.modifiers.maxWidth
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.compose.ui.modifiers.textAlign
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.base
import com.varabyte.kobweb.silk.style.toModifier
import dev.icerock.moko.resources.desc.StringDesc
import me.bookk.site.designsystem.components.Heading
import me.bookk.site.designsystem.components.HeadingLevel
import me.bookk.site.designsystem.components.TextBlock
import me.bookk.site.designsystem.components.TextLink
import me.bookk.site.designsystem.resources.WebLocalization
import me.bookk.site.designsystem.theme.BodyLargeTextStyle
import me.bookk.site.designsystem.theme.BodyMediumTextStyle
import me.bookk.site.designsystem.theme.HeadlineLargeTextStyle
import me.bookk.site.designsystem.theme.active
import me.bookk.site.designsystem.theme.secondary
import me.bookk.site.layout.Logo
import me.bookk.site.navigation.SiteLink
import org.jetbrains.compose.web.css.cssRem

val TelegramMessageStyle = CssStyle.base {
    Modifier
        .fillMaxWidth()
        .maxWidth(32.cssRem)
        .padding(leftRight = 1.5.cssRem, topBottom = 3.cssRem)
        .gap(1.cssRem)
        .textAlign(TextAlign.Center)
}

@Composable
fun TelegramMessage(title: StringDesc, body: StringDesc, link: SiteLink? = null) {
    Column(
        TelegramMessageStyle.toModifier(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Logo(Modifier.height(2.5.cssRem))
        Heading(title, HeadingLevel.H1, HeadlineLargeTextStyle.toModifier())
        TextBlock(
            body,
            BodyLargeTextStyle
                .toModifier()
                .secondary(),
        )
        link?.let { MessageLink(it) }
    }
}

@Composable
private fun MessageLink(link: SiteLink) {
    TextLink(
        link.title,
        WebLocalization.localize(link.path),
        BodyMediumTextStyle
            .toModifier()
            .active(),
    )
}
