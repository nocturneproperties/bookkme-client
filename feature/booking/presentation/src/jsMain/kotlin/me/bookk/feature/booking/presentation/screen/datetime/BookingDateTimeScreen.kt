package me.bookk.feature.booking.presentation.screen.datetime

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.BoxSizing
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.css.TextDecorationLine
import com.varabyte.kobweb.compose.foundation.layout.Arrangement
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.boxSizing
import com.varabyte.kobweb.compose.ui.modifiers.display
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.flex
import com.varabyte.kobweb.compose.ui.modifiers.flexGrow
import com.varabyte.kobweb.compose.ui.modifiers.fontWeight
import com.varabyte.kobweb.compose.ui.modifiers.gap
import com.varabyte.kobweb.compose.ui.modifiers.margin
import com.varabyte.kobweb.compose.ui.modifiers.maxWidth
import com.varabyte.kobweb.compose.ui.modifiers.minWidth
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.compose.ui.modifiers.position
import com.varabyte.kobweb.compose.ui.modifiers.textDecorationLine
import com.varabyte.kobweb.compose.ui.styleModifier
import com.varabyte.kobweb.compose.ui.toAttrs
import com.varabyte.kobweb.silk.style.toModifier
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import library.telegram.TelegramAppTopBar
import library.telegram.TelegramMainButton
import library.telegram.TelegramWebApp
import me.bookk.android.feature.booking.resources.BookingRes
import me.bookk.designsystem.components.Heading
import me.bookk.designsystem.components.HeadingLevel
import me.bookk.designsystem.components.List
import me.bookk.designsystem.components.NativeDatePicker
import me.bookk.designsystem.components.SelectableCardButton
import me.bookk.designsystem.components.TextBlock
import me.bookk.designsystem.components.TextButton
import me.bookk.designsystem.components.VerticalDivider
import me.bookk.designsystem.theme.BodyLargeTextStyle
import me.bookk.designsystem.theme.BodyMediumTextStyle
import me.bookk.designsystem.theme.HeadlineLargeTextStyle
import me.bookk.designsystem.theme.HeadlineSmallTextStyle
import me.bookk.designsystem.theme.LabelMediumTextStyle
import me.bookk.designsystem.theme.TitleMediumTextStyle
import me.bookk.designsystem.theme.active
import me.bookk.designsystem.theme.secondary
import me.bookk.feature.booking.presentation.screen.BookingTotalBar
import org.jetbrains.compose.web.css.DisplayStyle
import org.jetbrains.compose.web.css.Position
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.Div

private val BookingDateTimeModifier = Modifier
    .fillMaxWidth()
    .flexGrow(1)
    .maxWidth(40.cssRem)
    .boxSizing(BoxSizing.BorderBox)
    .padding(top = 0.5.cssRem, bottom = 1.5.cssRem, leftRight = 1.cssRem)
    .gap(1.5.cssRem)

private const val SLOT_MIN_COLUMN_REM = 4.5

private val SlotGridModifier = Modifier
    .fillMaxWidth()
    .display(DisplayStyle.Grid)
    .gap(0.5.cssRem)
    .styleModifier {
        property(
            "grid-template-columns",
            "repeat(auto-fill, minmax(${SLOT_MIN_COLUMN_REM}rem, 1fr))",
        )
    }

@Composable
internal fun BookingDateTimeScreen(state: BookingDateTimeState, webApp: TelegramWebApp) {
    TelegramAppTopBar(webApp, state.appBar)
    TelegramMainButton(webApp, state.continueButton)
    Column(BookingDateTimeModifier) {
        Title(state)
        Column(Modifier.fillMaxWidth().gap(0.75.cssRem)) {
            WeekHeader(state)
            WeekStrip(state.days)
        }
        List(state.slots, key = { it.id }) { SlotGroup(it) }
    }
    BookingTotalBar(state.total)
}

@Composable
private fun Title(state: BookingDateTimeState) {
    Column(Modifier.fillMaxWidth().gap(0.25.cssRem)) {
        Row(
            Modifier.fillMaxWidth().gap(0.5.cssRem),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            state.businessName?.let { SecondaryLabel(it) }
            state.specialistName?.let {
                VerticalDivider()
                SecondaryLabel(it)
            }
        }
        Heading(
            BookingRes.strings.booking_date_time_title.desc(),
            HeadingLevel.H1,
            HeadlineLargeTextStyle
                .toModifier()
                .fontWeight(FontWeight.ExtraBold)
                .margin(0.px),
        )
    }
}

@Composable
private fun SecondaryLabel(text: StringDesc) {
    TextBlock(
        text,
        BodyMediumTextStyle
            .toModifier()
            .fontWeight(FontWeight.SemiBold)
            .minWidth(0.px)
            .margin(0.px)
            .secondary(),
    )
}

@Composable
private fun WeekHeader(state: BookingDateTimeState) {
    Row(
        Modifier.fillMaxWidth().gap(0.5.cssRem),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        state.monthTitle?.let {
            TextBlock(
                it,
                TitleMediumTextStyle
                    .toModifier()
                    .fontWeight(FontWeight.SemiBold)
                    .margin(0.px)
                    .secondary(),
            )
        }
        Box(Modifier.position(Position.Relative)) {
            TextButton(state.pickDateButton, Modifier.padding(leftRight = 0.px))
            NativeDatePicker(state.datePicker)
        }
    }
}

@Composable
private fun WeekStrip(days: List<BookingDayItem>) {
    Row(Modifier.fillMaxWidth().gap(0.375.cssRem)) {
        days.forEach { DayTile(it) }
    }
}

@Composable
private fun DayTile(day: BookingDayItem) {
    SelectableCardButton(
        isSelected = day.isSelected,
        onClick = day.onClick,
        isEnabled = day.isEnabled,
        modifier = Modifier
            .flex(1)
            .minWidth(0.px)
            .padding(topBottom = 0.625.cssRem, leftRight = 0.px),
    ) {
        TextBlock(
            day.weekday,
            LabelMediumTextStyle
                .toModifier()
                .fontWeight(FontWeight.SemiBold)
                .margin(0.px)
                .dayTone(day),
        )
        TextBlock(
            day.dayOfMonth,
            HeadlineSmallTextStyle
                .toModifier()
                .fontWeight(FontWeight.Bold)
                .margin(0.px)
                .dayTone(day),
        )
    }
}

@Composable
private fun Modifier.dayTone(day: BookingDayItem): Modifier {
    return when {
        day.isClosed -> secondary().textDecorationLine(TextDecorationLine.LineThrough)
        day.isSelected -> active()
        else -> this
    }
}

@Composable
private fun SlotGroup(group: SlotGroupItem) {
    Column(Modifier.fillMaxWidth().gap(0.5.cssRem)) {
        TextBlock(
            group.title,
            TitleMediumTextStyle
                .toModifier()
                .fontWeight(FontWeight.SemiBold)
                .margin(0.px)
                .secondary(),
        )
        Div(SlotGridModifier.toAttrs()) {
            group.slots.forEach { SlotChip(it) }
        }
    }
}

@Composable
private fun SlotChip(slot: SlotItem) {
    SelectableCardButton(
        isSelected = slot.isSelected,
        onClick = slot.onClick,
        modifier = Modifier
            .minWidth(0.px)
            .padding(topBottom = 0.75.cssRem, leftRight = 0.px),
    ) {
        TextBlock(
            slot.time,
            BodyLargeTextStyle
                .toModifier()
                .fontWeight(FontWeight.Bold)
                .margin(0.px)
                .then(if (slot.isSelected) Modifier.active() else Modifier),
        )
    }
}
