package me.bookk.feature.booking.presentation.screen.bookings

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.BoxSizing
import com.varabyte.kobweb.compose.css.FontWeight
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
import com.varabyte.kobweb.compose.ui.modifiers.minWidth
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.compose.ui.modifiers.size
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.GeneralKind
import com.varabyte.kobweb.silk.style.toModifier
import dev.icerock.moko.resources.desc.desc
import library.telegram.TelegramAppTopBar
import library.telegram.TelegramWebApp
import me.bookk.android.designsystem.resources.DesignSystem
import me.bookk.android.feature.booking.resources.BookingRes
import me.bookk.designsystem.components.CardButton
import me.bookk.designsystem.components.DayDate
import me.bookk.designsystem.components.Heading
import me.bookk.designsystem.components.HeadingLevel
import me.bookk.designsystem.components.List
import me.bookk.designsystem.components.SegmentedControl
import me.bookk.designsystem.components.StatusLabel
import me.bookk.designsystem.components.TextBlock
import me.bookk.designsystem.components.TintedIcon
import me.bookk.designsystem.resources.color.ColorToken
import me.bookk.designsystem.theme.BodyLargeTextStyle
import me.bookk.designsystem.theme.HeadlineLargeTextStyle
import me.bookk.designsystem.theme.TitleMediumTextStyle
import me.bookk.designsystem.theme.secondary
import me.bookk.designsystem.uistate.ListState
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.px

private val BookingsModifier = Modifier
    .fillMaxWidth()
    .flexGrow(1)
    .maxWidth(40.cssRem)
    .boxSizing(BoxSizing.BorderBox)
    .padding(top = 0.75.cssRem, bottom = 1.5.cssRem, leftRight = 1.cssRem)
    .gap(1.125.cssRem)

@Composable
internal fun BookingsScreen(state: BookingsState, webApp: TelegramWebApp) {
    TelegramAppTopBar(webApp, state.appBar)
    Column(BookingsModifier) {
        Heading(
            BookingRes.strings.booking_bookings_title.desc(),
            HeadingLevel.H1,
            textModifier(HeadlineLargeTextStyle).fontWeight(FontWeight.ExtraBold),
        )
        SegmentedControl(
            BookingRes.strings.booking_bookings_tabs.desc(),
            listOf(state.upcomingTab, state.pastTab),
        )
        BookingsList(state.upcoming)
        BookingsList(state.past)
    }
}

@Composable
private fun BookingsList(state: ListState<BookingsListItem>) {
    List(state, key = { it.key }, modifier = Modifier.gap(0.625.cssRem)) { item ->
        when (item) {
            is BookingsListItem.Month -> MonthTitle(item)
            is BookingsListItem.Booking -> BookingCard(item)
        }
    }
}

@Composable
private fun MonthTitle(month: BookingsListItem.Month) {
    Heading(
        month.title.desc(),
        HeadingLevel.H2,
        textModifier(BodyLargeTextStyle)
            .fontWeight(FontWeight.SemiBold)
            .padding(top = 0.5.cssRem, left = 0.25.cssRem)
            .secondary(),
    )
}

@Composable
private fun BookingCard(item: BookingsListItem.Booking) {
    CardButton(
        onClick = item.onClick,
        modifier = Modifier.padding(topBottom = 1.cssRem, leftRight = 0.75.cssRem),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        DayDate(item.weekday, item.day, dayColor = item.dayColor)
        Column(
            Modifier
                .flexGrow(1)
                .minWidth(0.px)
                .gap(0.125.cssRem),
        ) {
            TextBlock(
                item.businessName.desc(),
                textModifier(TitleMediumTextStyle).fontWeight(FontWeight.SemiBold),
            )
            TextBlock(item.details, textModifier(BodyLargeTextStyle).secondary())
            StatusLabel(item.status, item.statusColor, Modifier.margin(top = 0.25.cssRem))
        }
        TintedIcon(
            DesignSystem.images.material_chevron_right,
            ColorToken.SecondaryText,
            Modifier.size(1.25.cssRem),
        )
    }
}

@Composable
private fun textModifier(style: CssStyle<GeneralKind>): Modifier {
    return style
        .toModifier()
        .margin(0.px)
}
