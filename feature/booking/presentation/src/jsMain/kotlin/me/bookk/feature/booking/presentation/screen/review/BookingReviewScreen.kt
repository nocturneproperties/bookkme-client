package me.bookk.feature.booking.presentation.screen.review

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.BoxSizing
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.css.TextAlign
import com.varabyte.kobweb.compose.css.TextDecorationLine
import com.varabyte.kobweb.compose.foundation.layout.Arrangement
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.boxSizing
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.flexGrow
import com.varabyte.kobweb.compose.ui.modifiers.flexShrink
import com.varabyte.kobweb.compose.ui.modifiers.fontWeight
import com.varabyte.kobweb.compose.ui.modifiers.gap
import com.varabyte.kobweb.compose.ui.modifiers.margin
import com.varabyte.kobweb.compose.ui.modifiers.maxWidth
import com.varabyte.kobweb.compose.ui.modifiers.minWidth
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.compose.ui.modifiers.textAlign
import com.varabyte.kobweb.compose.ui.modifiers.textDecorationLine
import com.varabyte.kobweb.compose.ui.thenIf
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.GeneralKind
import com.varabyte.kobweb.silk.style.toModifier
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import library.telegram.TelegramAppTopBar
import library.telegram.TelegramMainButton
import library.telegram.TelegramWebApp
import me.bookk.android.feature.booking.resources.BookingRes
import me.bookk.designsystem.components.AppCard
import me.bookk.designsystem.components.Heading
import me.bookk.designsystem.components.HeadingLevel
import me.bookk.designsystem.components.HorizontalDivider
import me.bookk.designsystem.components.InfoNote
import me.bookk.designsystem.components.TextBlock
import me.bookk.designsystem.components.TextField
import me.bookk.designsystem.theme.BodyMediumTextStyle
import me.bookk.designsystem.theme.BodySmallTextStyle
import me.bookk.designsystem.theme.HeadlineLargeTextStyle
import me.bookk.designsystem.theme.HeadlineSmallTextStyle
import me.bookk.designsystem.theme.TitleMediumTextStyle
import me.bookk.designsystem.theme.secondary
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.px

private const val NOTE_MIN_LINES = 3

private val BookingReviewModifier = Modifier
    .fillMaxWidth()
    .flexGrow(1)
    .maxWidth(40.cssRem)
    .boxSizing(BoxSizing.BorderBox)
    .padding(top = 0.5.cssRem, bottom = 1.5.cssRem, leftRight = 1.cssRem)
    .gap(1.5.cssRem)

@Composable
internal fun BookingReviewScreen(state: BookingReviewState, webApp: TelegramWebApp) {
    TelegramAppTopBar(webApp, state.appBar)
    TelegramMainButton(webApp, state.sendButton)
    Column(BookingReviewModifier) {
        Title(state.businessName)
        state.summary?.let { SummaryCard(it) }
        state.businessMessage?.let { InfoNote(it.text, title = it.title) }
        TextField(state.note, minLines = NOTE_MIN_LINES)
        state.confirmationNotice?.let { ConfirmationNotice(it) }
    }
}

@Composable
private fun Title(businessName: StringDesc?) {
    Column(Modifier.fillMaxWidth().gap(0.25.cssRem)) {
        businessName?.let {
            TextBlock(
                it,
                textModifier(BodyMediumTextStyle)
                    .fontWeight(FontWeight.SemiBold)
                    .minWidth(0.px)
                    .secondary(),
            )
        }
        Heading(
            BookingRes.strings.booking_review_title.desc(),
            HeadingLevel.H1,
            textModifier(HeadlineLargeTextStyle).fontWeight(FontWeight.ExtraBold),
        )
    }
}

@Composable
private fun SummaryCard(summary: ReviewSummary) {
    AppCard(Modifier.fillMaxWidth().boxSizing(BoxSizing.BorderBox).gap(1.cssRem)) {
        Column(Modifier.fillMaxWidth().gap(0.25.cssRem)) {
            TextBlock(summary.day, textModifier(HeadlineSmallTextStyle).fontWeight(FontWeight.Bold))
            TextBlock(summary.details, textModifier(BodyMediumTextStyle).secondary())
        }
        HorizontalDivider()
        Column(Modifier.fillMaxWidth().gap(0.5.cssRem)) {
            summary.services.forEach { ServiceRow(it) }
        }
        HorizontalDivider()
        TotalRow(summary.total)
    }
}

@Composable
private fun ServiceRow(service: ReviewServiceItem) {
    SpacedRow {
        TextBlock(service.name, textModifier(BodyMediumTextStyle).minWidth(0.px))
        ServicePrice(service)
    }
}

@Composable
private fun ServicePrice(service: ReviewServiceItem) {
    Row(
        Modifier.flexShrink(0).gap(0.5.cssRem),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        service.previousPrice?.let {
            TextBlock(
                it,
                textModifier(BodyMediumTextStyle)
                    .textDecorationLine(TextDecorationLine.LineThrough)
                    .secondary(),
            )
        }
        TextBlock(
            service.price,
            textModifier(BodyMediumTextStyle).thenIf(service.previousPrice != null) {
                Modifier.fontWeight(FontWeight.Bold)
            },
        )
    }
}

@Composable
private fun TotalRow(total: StringDesc) {
    SpacedRow {
        TextBlock(
            BookingRes.strings.booking_review_total.desc(),
            textModifier(TitleMediumTextStyle).fontWeight(FontWeight.SemiBold),
        )
        TextBlock(
            total,
            textModifier(HeadlineSmallTextStyle)
                .fontWeight(FontWeight.Bold)
                .flexShrink(0),
        )
    }
}

@Composable
private fun SpacedRow(content: @Composable () -> Unit) {
    Row(
        Modifier.fillMaxWidth().gap(1.cssRem),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        content()
    }
}

@Composable
private fun ConfirmationNotice(text: StringDesc) {
    TextBlock(
        text,
        textModifier(BodySmallTextStyle)
            .fillMaxWidth()
            .textAlign(TextAlign.Center)
            .secondary(),
    )
}

@Composable
private fun textModifier(style: CssStyle<GeneralKind>): Modifier {
    return style
        .toModifier()
        .margin(0.px)
}
