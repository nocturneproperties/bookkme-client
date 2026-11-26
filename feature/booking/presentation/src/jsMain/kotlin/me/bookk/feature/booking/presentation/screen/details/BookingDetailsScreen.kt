package me.bookk.feature.booking.presentation.screen.details

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.BoxSizing
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.css.OverflowWrap
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
import com.varabyte.kobweb.compose.ui.modifiers.overflowWrap
import com.varabyte.kobweb.compose.ui.modifiers.padding
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
import me.bookk.designsystem.components.Header
import me.bookk.designsystem.components.Heading
import me.bookk.designsystem.components.HeadingLevel
import me.bookk.designsystem.components.InfoCard
import me.bookk.designsystem.components.StatusPill
import me.bookk.designsystem.components.TextBlock
import me.bookk.designsystem.theme.BodyMediumTextStyle
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
        InfoCard(booking.rows)
        ServicesSection(booking)
        TextBlock(
            booking.statusExplanation,
            textModifier(BodyMediumTextStyle)
                .padding(leftRight = 1.cssRem)
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
private fun ServicesSection(booking: BookingDetailsItem) {
    Column(Modifier.fillMaxWidth().gap(0.5.cssRem)) {
        Header(
            BookingRes.strings.booking_details_services.desc(),
            Modifier
                .margin(0.px)
                .padding(leftRight = 1.cssRem),
        )
        InfoCard(booking.services)
        TotalRow(booking.total)
    }
}

@Composable
private fun TotalRow(total: StringDesc) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(top = 0.5.cssRem, leftRight = 1.cssRem)
            .gap(1.cssRem),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
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
private fun textModifier(style: CssStyle<GeneralKind>): Modifier {
    return style
        .toModifier()
        .margin(0.px)
}
