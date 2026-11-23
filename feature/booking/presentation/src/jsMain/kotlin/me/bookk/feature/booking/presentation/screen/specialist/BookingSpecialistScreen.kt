package me.bookk.feature.booking.presentation.screen.specialist

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.BoxSizing
import com.varabyte.kobweb.compose.css.FontWeight
import com.varabyte.kobweb.compose.css.TextAlign
import com.varabyte.kobweb.compose.foundation.layout.Column
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
import com.varabyte.kobweb.silk.style.toModifier
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import library.telegram.TelegramAppTopBar
import library.telegram.TelegramMainButton
import library.telegram.TelegramWebApp
import me.bookk.android.feature.booking.resources.BookingRes
import me.bookk.feature.booking.presentation.screen.BookingTotalBar
import me.bookk.site.designsystem.components.CardGrid
import me.bookk.site.designsystem.components.Heading
import me.bookk.site.designsystem.components.HeadingLevel
import me.bookk.site.designsystem.components.InitialsAvatar
import me.bookk.site.designsystem.components.InitialsAvatarSize
import me.bookk.site.designsystem.components.SelectableCardButton
import me.bookk.site.designsystem.components.TextBlock
import me.bookk.site.designsystem.theme.BodyLargeTextStyle
import me.bookk.site.designsystem.theme.BodyMediumTextStyle
import me.bookk.site.designsystem.theme.HeadlineLargeTextStyle
import me.bookk.site.designsystem.theme.secondary
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.px

private val BookingSpecialistModifier = Modifier
    .fillMaxWidth()
    .flexGrow(1)
    .maxWidth(40.cssRem)
    .boxSizing(BoxSizing.BorderBox)
    .padding(top = 0.5.cssRem, bottom = 1.5.cssRem, leftRight = 1.cssRem)
    .gap(1.5.cssRem)

@Composable
internal fun BookingSpecialistScreen(state: BookingSpecialistState, webApp: TelegramWebApp) {
    TelegramAppTopBar(webApp, state.appBar)
    TelegramMainButton(webApp, state.continueButton)
    Column(BookingSpecialistModifier) {
        Title(state.businessName)
        CardGrid(state.specialists, key = { it.id }) { SpecialistCard(it) }
    }
    BookingTotalBar(state.total)
}

@Composable
private fun Title(businessName: StringDesc?) {
    Column(
        Modifier
            .fillMaxWidth()
            .gap(0.25.cssRem),
    ) {
        businessName?.let {
            TextBlock(
                it,
                BodyMediumTextStyle
                    .toModifier()
                    .fontWeight(FontWeight.SemiBold)
                    .margin(0.px)
                    .secondary(),
            )
        }
        Heading(
            BookingRes.strings.booking_specialist_title.desc(),
            HeadingLevel.H1,
            HeadlineLargeTextStyle
                .toModifier()
                .fontWeight(FontWeight.ExtraBold)
                .margin(0.px),
        )
    }
}

@Composable
private fun SpecialistCard(item: SpecialistItem) {
    SelectableCardButton(
        isSelected = item.isSelected,
        onClick = item.onClick,
        modifier = Modifier.flexGrow(1),
    ) {
        InitialsAvatar(item.initials, size = InitialsAvatarSize.LARGE)
        TextBlock(
            item.name,
            BodyLargeTextStyle
                .toModifier()
                .fillMaxWidth()
                .fontWeight(FontWeight.Bold)
                .textAlign(TextAlign.Center)
                .margin(0.px),
        )
    }
}
