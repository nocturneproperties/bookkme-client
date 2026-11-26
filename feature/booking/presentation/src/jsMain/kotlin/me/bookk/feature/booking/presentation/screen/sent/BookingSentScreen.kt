package me.bookk.feature.booking.presentation.screen.sent

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.BoxSizing
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.css.TextAlign
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.boxSizing
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.flexGrow
import com.varabyte.kobweb.compose.ui.modifiers.fontWeight
import com.varabyte.kobweb.compose.ui.modifiers.gap
import com.varabyte.kobweb.compose.ui.modifiers.margin
import com.varabyte.kobweb.compose.ui.modifiers.maxWidth
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.compose.ui.modifiers.textAlign
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.GeneralKind
import com.varabyte.kobweb.silk.style.toModifier
import dev.icerock.moko.resources.desc.desc
import library.telegram.TelegramAppTopBar
import library.telegram.TelegramMainButton
import library.telegram.TelegramWebApp
import me.bookk.android.feature.booking.resources.BookingRes
import me.bookk.designsystem.components.CardButton
import me.bookk.designsystem.components.Heading
import me.bookk.designsystem.components.HeadingLevel
import me.bookk.designsystem.components.StatusPill
import me.bookk.designsystem.components.SuccessMark
import me.bookk.designsystem.components.TextBlock
import me.bookk.designsystem.components.TextButton
import me.bookk.designsystem.theme.BodyLargeTextStyle
import me.bookk.designsystem.theme.HeadlineLargeTextStyle
import me.bookk.designsystem.theme.HeadlineSmallTextStyle
import me.bookk.designsystem.theme.LeadTextStyle
import me.bookk.designsystem.theme.secondary
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.px

private val BookingSentModifier = Modifier
    .fillMaxWidth()
    .flexGrow(1)
    .maxWidth(40.cssRem)
    .boxSizing(BoxSizing.BorderBox)
    .padding(top = 3.5.cssRem, bottom = 1.5.cssRem, leftRight = 1.cssRem)
    .gap(1.25.cssRem)
    .textAlign(TextAlign.Center)

@Composable
internal fun BookingSentScreen(state: BookingSentState, webApp: TelegramWebApp) {
    TelegramAppTopBar(webApp, state.appBar)
    TelegramMainButton(webApp, state.doneButton)
    Column(BookingSentModifier, horizontalAlignment = Alignment.CenterHorizontally) {
        SuccessMark()
        Heading(
            BookingRes.strings.booking_sent_title.desc(),
            HeadingLevel.H1,
            textModifier(HeadlineLargeTextStyle).fontWeight(FontWeight.ExtraBold),
        )
        TextBlock(
            BookingRes.strings.booking_sent_message.desc(),
            textModifier(LeadTextStyle)
                .maxWidth(20.cssRem)
                .secondary(),
        )
        state.booking?.let { SentBookingCard(it) }
        TextButton(state.bookAgainButton)
    }
}

@Composable
private fun SentBookingCard(booking: SentBookingItem) {
    CardButton(
        onClick = booking.onClick,
        modifier = Modifier
            .fillMaxWidth()
            .margin(top = 0.75.cssRem)
            .textAlign(TextAlign.Start),
    ) {
        Column(Modifier.gap(0.75.cssRem)) {
            StatusPill(booking.status, booking.statusColor)
            Column(Modifier.gap(0.125.cssRem)) {
                TextBlock(
                    booking.day,
                    textModifier(HeadlineSmallTextStyle).fontWeight(FontWeight.Bold),
                )
                TextBlock(booking.details, textModifier(BodyLargeTextStyle).secondary())
            }
        }
    }
}

@Composable
private fun textModifier(style: CssStyle<GeneralKind>): Modifier {
    return style
        .toModifier()
        .margin(0.px)
}
