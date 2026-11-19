package me.bookk.site.landing.clients

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.JustifyContent
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.backgroundColor
import com.varabyte.kobweb.compose.ui.modifiers.borderRadius
import com.varabyte.kobweb.compose.ui.modifiers.borderTop
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.flexBasis
import com.varabyte.kobweb.compose.ui.modifiers.flexGrow
import com.varabyte.kobweb.compose.ui.modifiers.flexShrink
import com.varabyte.kobweb.compose.ui.modifiers.flexWrap
import com.varabyte.kobweb.compose.ui.modifiers.gap
import com.varabyte.kobweb.compose.ui.modifiers.justifyContent
import com.varabyte.kobweb.compose.ui.modifiers.margin
import com.varabyte.kobweb.compose.ui.modifiers.maxWidth
import com.varabyte.kobweb.compose.ui.modifiers.minWidth
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.base
import com.varabyte.kobweb.silk.style.toModifier
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import me.bookk.designsystem.resources.color.ColorToken
import me.bookk.site.designsystem.components.Heading
import me.bookk.site.designsystem.components.HeadingLevel
import me.bookk.site.designsystem.components.IconTile
import me.bookk.site.designsystem.components.MediaFrame
import me.bookk.site.designsystem.components.MediaPlaceholder
import me.bookk.site.designsystem.components.SectionContainer
import me.bookk.site.designsystem.components.TextBlock
import me.bookk.site.designsystem.components.TextLink
import me.bookk.site.designsystem.resources.WebLocalization
import me.bookk.site.designsystem.theme.BandTone
import me.bookk.site.designsystem.theme.BodyLargeTextStyle
import me.bookk.site.designsystem.theme.BodyMediumTextStyle
import me.bookk.site.designsystem.theme.HeadlineLargeTextStyle
import me.bookk.site.designsystem.theme.LabelMediumTextStyle
import me.bookk.site.designsystem.theme.TitleMediumTextStyle
import me.bookk.site.designsystem.theme.active
import me.bookk.site.designsystem.theme.resolve
import me.bookk.site.designsystem.theme.secondary
import me.bookk.site.designsystem.theme.webColors
import me.bookk.site.landing.LandingBand
import me.bookk.site.landing.LandingSection
import me.bookk.site.landing.PersonalKeyDiagram
import me.bookk.site.landing.PrivacyPanel
import me.bookk.site.landing.SectionIntro
import me.bookk.site.landing.Statement
import me.bookk.site.landing.business.SplitSectionStyle
import me.bookk.site.navigation.SiteNavigation
import me.bookk.site.resources.SiteRes
import org.jetbrains.compose.web.css.FlexWrap
import org.jetbrains.compose.web.css.LineStyle
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.px

val BookingWayStyle = CssStyle.base {
    Modifier
        .flexGrow(1)
        .flexShrink(1)
        .flexBasis(22.cssRem)
        .minWidth(0.px)
        .maxWidth(30.cssRem)
        .gap(1.cssRem)
        .padding(1.75.cssRem)
        .borderRadius(1.5.cssRem)
        .backgroundColor(ColorToken.Elevated.resolve(colorMode.webColors()))
}

val ClientTextColumnStyle = CssStyle.base {
    Modifier
        .flexGrow(1)
        .flexShrink(1)
        .flexBasis(22.cssRem)
        .minWidth(0.px)
        .gap(1.cssRem)
}

val ClientVisualColumnStyle = CssStyle.base {
    Modifier
        .flexGrow(1)
        .flexShrink(1)
        .flexBasis(26.cssRem)
        .minWidth(0.px)
}

val PromiseRowStyle = CssStyle.base {
    Modifier
        .fillMaxWidth()
        .gap(1.cssRem)
        .padding(topBottom = 1.5.cssRem)
}

val DividedPromiseRowStyle = CssStyle.base {
    Modifier.borderTop(1.px, LineStyle.Solid, ColorToken.Divider.resolve(colorMode.webColors()))
}

@Composable
fun BookingWaysSection() {
    SectionContainer(Modifier.margin(bottom = 1.5.cssRem)) {
        Row(
            Modifier
                .fillMaxWidth()
                .gap(2.cssRem)
                .flexWrap(FlexWrap.Wrap)
                .justifyContent(JustifyContent.Center),
        ) {
            BookingWay(
                SiteRes.strings.site_clients_browser_title.desc(),
                SiteRes.strings.site_clients_browser_caption.desc(),
                MediaFrame.BROWSER,
            )
            BookingWay(
                SiteRes.strings.site_clients_telegram_title.desc(),
                SiteRes.strings.site_clients_telegram_caption.desc(),
                MediaFrame.PANEL,
            )
        }
    }
}

@Composable
private fun BookingWay(title: StringDesc, caption: StringDesc, frame: MediaFrame) {
    Column(BookingWayStyle.toModifier()) {
        Row(
            Modifier
                .fillMaxWidth()
                .gap(0.75.cssRem)
                .flexWrap(FlexWrap.Wrap)
                .justifyContent(JustifyContent.SpaceBetween),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Heading(title, HeadingLevel.H2, TitleMediumTextStyle.toModifier())
            TextBlock(
                caption,
                LabelMediumTextStyle
                    .toModifier()
                    .secondary(),
            )
        }
        MediaPlaceholder(SiteRes.strings.site_media_coming_soon.desc(), frame, Modifier.flexGrow(1))
    }
}

@Composable
fun ClientPromisesSection() {
    LandingBand(LandingSection.BOOKING, BandTone.ALTERNATE) {
        Row(SplitSectionStyle.toModifier(), verticalAlignment = Alignment.Top) {
            Column(ClientTextColumnStyle.toModifier()) {
                SectionIntro(
                    SiteRes.strings.site_clients_booking_title.desc(),
                    SiteRes.strings.site_clients_booking_subtitle.desc(),
                )
            }
            Column(ClientVisualColumnStyle.toModifier()) {
                ClientContent.promises.forEachIndexed { index, promise ->
                    PromiseRow(promise, isDivided = index > 0)
                }
            }
        }
    }
}

@Composable
private fun PromiseRow(promise: Promise, isDivided: Boolean) {
    val divider = if (isDivided) DividedPromiseRowStyle.toModifier() else Modifier
    Row(
        PromiseRowStyle
            .toModifier()
            .then(divider),
        verticalAlignment = Alignment.Top,
    ) {
        IconTile(promise.icon)
        Column(Modifier.gap(0.375.cssRem)) {
            Heading(promise.title, HeadingLevel.H3, TitleMediumTextStyle.toModifier())
            TextBlock(
                promise.description,
                BodyMediumTextStyle
                    .toModifier()
                    .secondary(),
            )
        }
    }
}

@Composable
fun AnytimeBookingSection() {
    LandingBand(null) {
        Row(SplitSectionStyle.toModifier(), verticalAlignment = Alignment.CenterVertically) {
            Column(ClientTextColumnStyle.toModifier()) {
                Heading(
                    SiteRes.strings.site_clients_anytime_title.desc(),
                    HeadingLevel.H2,
                    HeadlineLargeTextStyle.toModifier(),
                )
                TextBlock(
                    SiteRes.strings.site_clients_anytime_description.desc(),
                    BodyLargeTextStyle
                        .toModifier()
                        .secondary(),
                )
            }
            MediaPlaceholder(
                SiteRes.strings.site_media_coming_soon.desc(),
                MediaFrame.PANEL,
                ClientVisualColumnStyle.toModifier(),
            )
        }
    }
}

@Composable
fun ClientPrivacySection() {
    PrivacyPanel(
        SiteRes.strings.site_clients_privacy_subtitle.desc(),
        ClientContent.privacy,
    ) {
        PersonalKeyDiagram(ClientContent.keyFields, it)
    }
}

@Composable
fun LeavingStatement() {
    Statement(
        SiteRes.strings.site_clients_leave_title_start.desc(),
        SiteRes.strings.site_clients_leave_title_accent.desc(),
        SiteRes.strings.site_clients_leave_description.desc(),
    )
}
