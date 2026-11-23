package me.bookk.feature.booking.presentation.screen.services

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.BoxSizing
import com.varabyte.kobweb.compose.css.FontWeight
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
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.GeneralKind
import com.varabyte.kobweb.silk.style.toModifier
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import library.telegram.TelegramAppTopBar
import library.telegram.TelegramMainButton
import library.telegram.TelegramWebApp
import me.bookk.android.feature.booking.resources.BookingRes
import me.bookk.site.designsystem.components.Heading
import me.bookk.site.designsystem.components.HeadingLevel
import me.bookk.site.designsystem.components.List
import me.bookk.site.designsystem.components.OptionsQuantityList
import me.bookk.site.designsystem.components.RevealingBottomBar
import me.bookk.site.designsystem.components.TextBlock
import me.bookk.site.designsystem.theme.BodyMediumTextStyle
import me.bookk.site.designsystem.theme.BodySmallTextStyle
import me.bookk.site.designsystem.theme.HeadlineLargeTextStyle
import me.bookk.site.designsystem.theme.HeadlineSmallTextStyle
import me.bookk.site.designsystem.theme.secondary
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.px

private val BookingServicesModifier = Modifier
    .fillMaxWidth()
    .flexGrow(1)
    .maxWidth(40.cssRem)
    .boxSizing(BoxSizing.BorderBox)
    .padding(top = 0.5.cssRem, bottom = 1.5.cssRem, leftRight = 1.cssRem)
    .gap(1.5.cssRem)

private val TotalContentModifier = Modifier
    .fillMaxWidth()
    .maxWidth(40.cssRem)
    .boxSizing(BoxSizing.BorderBox)
    .padding(topBottom = 0.75.cssRem, leftRight = 1.25.cssRem)
    .gap(1.cssRem)

@Composable
internal fun BookingServicesScreen(state: BookingServicesState, webApp: TelegramWebApp) {
    TelegramAppTopBar(webApp, state.appBar)
    TelegramMainButton(webApp, state.continueButton)
    Column(BookingServicesModifier) {
        Title(state.businessName)
        List(state.groups, key = { it.id }, Modifier.gap(1.5.cssRem)) { group ->
            OptionsQuantityList(state.services, group.name, group.services) { ServiceRow(it) }
        }
    }
    RevealingBottomBar(state.total) { TotalBar(it) }
}

@Composable
private fun Title(businessName: StringDesc?) {
    Column(
        Modifier
            .fillMaxWidth()
            .gap(0.25.cssRem),
    ) {
        businessName?.let {
            SecondaryText(
                it,
                BodyMediumTextStyle,
                Modifier.fontWeight(FontWeight.SemiBold),
            )
        }
        Heading(
            BookingRes.strings.booking_services_title.desc(),
            HeadingLevel.H1,
            HeadlineLargeTextStyle
                .toModifier()
                .fontWeight(FontWeight.ExtraBold)
                .margin(0.px),
        )
    }
}

@Composable
private fun ServiceRow(option: ServiceOption) {
    Column(
        Modifier
            .minWidth(0.px)
            .gap(0.125.cssRem),
    ) {
        TextBlock(
            option.displayName,
            BodyMediumTextStyle
                .toModifier()
                .fontWeight(FontWeight.SemiBold)
                .margin(0.px),
        )
        SecondaryText(option.details, BodySmallTextStyle)
    }
}

@Composable
private fun TotalBar(total: ServicesTotal) {
    Row(
        TotalContentModifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SecondaryText(total.summary, BodyMediumTextStyle, Modifier.minWidth(0.px))
        TextBlock(
            total.price,
            HeadlineSmallTextStyle
                .toModifier()
                .fontWeight(FontWeight.Bold)
                .flexShrink(0)
                .margin(0.px),
        )
    }
}

@Composable
private fun SecondaryText(
    text: StringDesc,
    style: CssStyle<GeneralKind>,
    modifier: Modifier = Modifier,
) {
    TextBlock(
        text,
        style
            .toModifier()
            .margin(0.px)
            .secondary()
            .then(modifier),
    )
}
