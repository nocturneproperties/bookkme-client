package me.bookk.client.telegram

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
import com.varabyte.kobweb.compose.ui.styleModifier
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.base
import com.varabyte.kobweb.silk.style.toModifier
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import me.bookk.client.navigation.ClientLink
import me.bookk.client.resources.ClientRes
import me.bookk.designsystem.components.Heading
import me.bookk.designsystem.components.HeadingLevel
import me.bookk.designsystem.components.Logo
import me.bookk.designsystem.components.TextBlock
import me.bookk.designsystem.components.TextLink
import me.bookk.designsystem.theme.BodyLargeTextStyle
import me.bookk.designsystem.theme.BodyMediumTextStyle
import me.bookk.designsystem.theme.HeadlineLargeTextStyle
import me.bookk.designsystem.theme.active
import me.bookk.designsystem.theme.secondary
import org.jetbrains.compose.web.css.cssRem

val TelegramMessageStyle = CssStyle.base {
    Modifier
        .fillMaxWidth()
        .maxWidth(32.cssRem)
        .padding(leftRight = 1.5.cssRem, topBottom = 3.cssRem)
        .styleModifier { property("margin-block", "auto") }
        .gap(1.cssRem)
        .textAlign(TextAlign.Center)
}

val TelegramFormStyle = CssStyle.base {
    Modifier
        .fillMaxWidth()
        .maxWidth(40.cssRem)
        .padding(top = 1.5.cssRem, bottom = 1.5.cssRem, leftRight = 1.cssRem)
        .gap(1.5.cssRem)
}

@Composable
fun TelegramMessage(title: StringDesc, body: StringDesc, link: ClientLink? = null) {
    TelegramMessageLayout {
        Heading(title, HeadingLevel.H1, HeadlineLargeTextStyle.toModifier())
        TelegramMessageBody(body)
        link?.let { MessageLink(it) }
    }
}

@Composable
fun TelegramMessageLayout(content: @Composable () -> Unit) {
    Column(
        TelegramMessageStyle.toModifier(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Logo(ClientRes.strings.client_app_name.desc(), Modifier.height(2.5.cssRem))
        content()
    }
}

@Composable
fun TelegramFormLayout(content: @Composable () -> Unit) {
    Column(TelegramFormStyle.toModifier()) {
        Logo(ClientRes.strings.client_app_name.desc(), Modifier.height(2.5.cssRem))
        content()
    }
}

@Composable
fun TelegramMessageBody(text: StringDesc) {
    TextBlock(
        text,
        BodyLargeTextStyle
            .toModifier()
            .secondary(),
    )
}

@Composable
private fun MessageLink(link: ClientLink) {
    TextLink(
        link.title,
        link.url,
        BodyMediumTextStyle
            .toModifier()
            .active(),
    )
}
