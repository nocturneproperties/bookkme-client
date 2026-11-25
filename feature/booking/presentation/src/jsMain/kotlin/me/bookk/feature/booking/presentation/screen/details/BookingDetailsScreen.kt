package me.bookk.feature.booking.presentation.screen.details

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.BoxSizing
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.css.OverflowWrap
import com.varabyte.kobweb.compose.css.TextAlign
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
import com.varabyte.kobweb.compose.ui.modifiers.overflowWrap
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.compose.ui.modifiers.textAlign
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.GeneralKind
import com.varabyte.kobweb.silk.style.toModifier
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import library.telegram.TelegramAppTopBar
import library.telegram.TelegramButtonStyle
import library.telegram.TelegramMainButton
import library.telegram.TelegramWebApp
import me.bookk.android.feature.booking.resources.BookingRes
import me.bookk.designsystem.components.AppCard
import me.bookk.designsystem.components.Heading
import me.bookk.designsystem.components.HeadingLevel
import me.bookk.designsystem.components.HorizontalDivider
import me.bookk.designsystem.components.StatusPill
import me.bookk.designsystem.components.TextBlock
import me.bookk.designsystem.theme.BodyMediumTextStyle
import me.bookk.designsystem.theme.BodySmallTextStyle
import me.bookk.designsystem.theme.HeadlineLargeTextStyle
import me.bookk.designsystem.theme.HeadlineSmallTextStyle
import me.bookk.designsystem.theme.TitleMediumTextStyle
import me.bookk.designsystem.theme.secondary
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.px

private val BookingDetailsModifier = Modifier
    .fillMaxWidth()
    .flexGrow(1)
    .maxWidth(40.cssRem)
    .boxSizing(BoxSizing.BorderBox)
    .padding(top = 0.75.cssRem, bottom = 1.5.cssRem, leftRight = 1.cssRem)
    .gap(1.cssRem)

@Composable
internal fun BookingDetailsScreen(state: BookingDetailsState, webApp: TelegramWebApp) {
    TelegramAppTopBar(webApp, state.appBar)
    TelegramMainButton(webApp, state.actionButton, TelegramButtonStyle.DESTRUCTIVE)
    state.booking?.let { BookingDetailsContent(it) }
}

@Composable
private fun BookingDetailsContent(booking: BookingDetailsItem) {
    Column(BookingDetailsModifier) {
        Title(booking)
        DetailsCard(booking)
        TextBlock(
            booking.statusExplanation,
            textModifier(BodySmallTextStyle)
                .padding(leftRight = 0.25.cssRem)
                .secondary(),
        )
    }
}

@Composable
private fun Title(booking: BookingDetailsItem) {
    Column(Modifier.fillMaxWidth().gap(0.625.cssRem)) {
        StatusPill(booking.status, booking.statusColor)
        Heading(
            booking.businessName,
            HeadingLevel.H1,
            textModifier(HeadlineLargeTextStyle)
                .fontWeight(FontWeight.ExtraBold)
                .overflowWrap(OverflowWrap.Anywhere),
        )
    }
}

@Composable
private fun DetailsCard(booking: BookingDetailsItem) {
    AppCard(Modifier.fillMaxWidth().boxSizing(BoxSizing.BorderBox).gap(0.875.cssRem)) {
        booking.rows.forEach { DetailsRow(it) }
        HorizontalDivider()
        Column(Modifier.fillMaxWidth().gap(0.5.cssRem)) {
            booking.services.forEach { ServiceRow(it) }
        }
        HorizontalDivider()
        TotalRow(booking.total)
    }
}

@Composable
private fun DetailsRow(row: BookingDetailsRow) {
    SpacedRow {
        TextBlock(row.title, textModifier(BodyMediumTextStyle).flexShrink(0).secondary())
        TextBlock(
            row.value,
            textModifier(BodyMediumTextStyle)
                .minWidth(0.px)
                .textAlign(TextAlign.End)
                .overflowWrap(OverflowWrap.Anywhere),
        )
    }
}

@Composable
private fun ServiceRow(service: BookingDetailsServiceItem) {
    SpacedRow {
        TextBlock(
            service.name,
            textModifier(BodyMediumTextStyle)
                .minWidth(0.px)
                .overflowWrap(OverflowWrap.Anywhere),
        )
        TextBlock(service.price, textModifier(BodyMediumTextStyle).flexShrink(0))
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
        verticalAlignment = Alignment.Top,
    ) {
        content()
    }
}

@Composable
private fun textModifier(style: CssStyle<GeneralKind>): Modifier {
    return style
        .toModifier()
        .margin(0.px)
}
