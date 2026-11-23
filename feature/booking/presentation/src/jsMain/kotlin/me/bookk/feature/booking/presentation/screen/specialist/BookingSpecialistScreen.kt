package me.bookk.feature.booking.presentation.screen.specialist

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.BoxSizing
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.boxSizing
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.flexGrow
import com.varabyte.kobweb.compose.ui.modifiers.gap
import com.varabyte.kobweb.compose.ui.modifiers.margin
import com.varabyte.kobweb.compose.ui.modifiers.maxWidth
import com.varabyte.kobweb.compose.ui.modifiers.minWidth
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.silk.style.toModifier
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import library.telegram.TelegramAppTopBar
import library.telegram.TelegramWebApp
import me.bookk.android.feature.booking.resources.BookingRes
import me.bookk.site.designsystem.components.CardList
import me.bookk.site.designsystem.components.Heading
import me.bookk.site.designsystem.components.HeadingLevel
import me.bookk.site.designsystem.components.InitialsAvatar
import me.bookk.site.designsystem.components.ListRowButton
import me.bookk.site.designsystem.components.TextBlock
import me.bookk.site.designsystem.theme.BodyLargeTextStyle
import me.bookk.site.designsystem.theme.BodyMediumTextStyle
import me.bookk.site.designsystem.theme.BodySmallTextStyle
import me.bookk.site.designsystem.theme.HeadlineSmallTextStyle
import me.bookk.site.designsystem.theme.secondary
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.px

private val BookingSpecialistModifier = Modifier
    .fillMaxWidth()
    .maxWidth(40.cssRem)
    .boxSizing(BoxSizing.BorderBox)
    .padding(top = 0.5.cssRem, bottom = 1.5.cssRem, leftRight = 1.cssRem)
    .gap(1.25.cssRem)

@Composable
internal fun BookingSpecialistScreen(state: BookingSpecialistState, webApp: TelegramWebApp) {
    TelegramAppTopBar(webApp, state.appBar)
    Column(BookingSpecialistModifier) {
        Title(state.summary)
        CardList(state.specialists, key = { it.id }) { SpecialistRow(it) }
        if (state.specialists.items.isNotEmpty()) Hint()
    }
}

@Composable
private fun Title(summary: StringDesc?) {
    Column(
        Modifier
            .fillMaxWidth()
            .gap(0.25.cssRem),
    ) {
        Heading(
            BookingRes.strings.booking_specialist_title.desc(),
            HeadingLevel.H1,
            HeadlineSmallTextStyle
                .toModifier()
                .margin(0.px),
        )
        summary?.let {
            TextBlock(
                it,
                BodyMediumTextStyle
                    .toModifier()
                    .margin(0.px)
                    .secondary(),
            )
        }
    }
}

@Composable
private fun SpecialistRow(item: SpecialistItem) {
    ListRowButton(onClick = item.onClick) {
        InitialsAvatar(item.initials)
        TextBlock(
            item.name,
            BodyLargeTextStyle
                .toModifier()
                .flexGrow(1)
                .minWidth(0.px)
                .margin(0.px),
        )
    }
}

@Composable
private fun Hint() {
    TextBlock(
        BookingRes.strings.booking_specialist_hint.desc(),
        BodySmallTextStyle
            .toModifier()
            .margin(leftRight = 0.25.cssRem)
            .secondary(),
    )
}
