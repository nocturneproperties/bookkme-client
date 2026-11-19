package me.bookk.site.landing.business

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.JustifyContent
import com.varabyte.kobweb.compose.css.Overflow
import com.varabyte.kobweb.compose.css.TextAlign
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.borderBottom
import com.varabyte.kobweb.compose.ui.modifiers.borderRadius
import com.varabyte.kobweb.compose.ui.modifiers.borderTop
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.flexBasis
import com.varabyte.kobweb.compose.ui.modifiers.flexGrow
import com.varabyte.kobweb.compose.ui.modifiers.flexShrink
import com.varabyte.kobweb.compose.ui.modifiers.flexWrap
import com.varabyte.kobweb.compose.ui.modifiers.gap
import com.varabyte.kobweb.compose.ui.modifiers.height
import com.varabyte.kobweb.compose.ui.modifiers.justifyContent
import com.varabyte.kobweb.compose.ui.modifiers.margin
import com.varabyte.kobweb.compose.ui.modifiers.maxWidth
import com.varabyte.kobweb.compose.ui.modifiers.minHeight
import com.varabyte.kobweb.compose.ui.modifiers.minWidth
import com.varabyte.kobweb.compose.ui.modifiers.overflow
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.compose.ui.modifiers.textAlign
import com.varabyte.kobweb.compose.ui.modifiers.width
import com.varabyte.kobweb.compose.ui.styleModifier
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.base
import com.varabyte.kobweb.silk.style.breakpoint.Breakpoint
import com.varabyte.kobweb.silk.style.breakpoint.displayIfAtLeast
import com.varabyte.kobweb.silk.style.toModifier
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import me.bookk.designsystem.resources.color.ColorToken
import me.bookk.site.designsystem.components.ButtonLink
import me.bookk.site.designsystem.components.ButtonLinkVariant
import me.bookk.site.designsystem.components.CardGallery
import me.bookk.site.designsystem.components.DividedRow
import me.bookk.site.designsystem.components.DividedRows
import me.bookk.site.designsystem.components.GalleryNavigation
import me.bookk.site.designsystem.components.Heading
import me.bookk.site.designsystem.components.HeadingLevel
import me.bookk.site.designsystem.components.MediaFrame
import me.bookk.site.designsystem.components.MediaPlaceholder
import me.bookk.site.designsystem.components.SectionContainer
import me.bookk.site.designsystem.components.StepCard
import me.bookk.site.designsystem.components.TextBlock
import me.bookk.site.designsystem.components.TextLink
import me.bookk.site.designsystem.resources.WebLocalization
import me.bookk.site.designsystem.theme.BandTone
import me.bookk.site.designsystem.theme.BodyLargeTextStyle
import me.bookk.site.designsystem.theme.BodyMediumTextStyle
import me.bookk.site.designsystem.theme.HeadlineLargeTextStyle
import me.bookk.site.designsystem.theme.HeadlineSmallTextStyle
import me.bookk.site.designsystem.theme.LabelMediumTextStyle
import me.bookk.site.designsystem.theme.TitleMediumTextStyle
import me.bookk.site.designsystem.theme.active
import me.bookk.site.designsystem.theme.resolve
import me.bookk.site.designsystem.theme.secondary
import me.bookk.site.designsystem.theme.webColors
import me.bookk.site.landing.FeatureGrid
import me.bookk.site.landing.FullBleedLandingBand
import me.bookk.site.landing.HeroActions
import me.bookk.site.landing.LandingBand
import me.bookk.site.landing.LandingSection
import me.bookk.site.landing.PrivacyPanel
import me.bookk.site.landing.SectionIntro
import me.bookk.site.landing.TeamKeyDiagram
import me.bookk.site.navigation.SiteNavigation
import me.bookk.site.resources.SiteRes
import org.jetbrains.compose.web.css.FlexWrap
import org.jetbrains.compose.web.css.LineStyle
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.px

val HeroStageStyle = CssStyle {
    base {
        Modifier
            .fillMaxWidth()
            .height(25.cssRem)
            .margin(top = 2.5.cssRem)
            .overflow(Overflow.Hidden)
            .justifyContent(JustifyContent.Center)
            .styleModifier {
                property("mask-image", HERO_STAGE_FADE)
                property("-webkit-mask-image", HERO_STAGE_FADE)
            }
    }
    Breakpoint.MD {
        Modifier
            .height(30.cssRem)
            .margin(top = 3.5.cssRem)
    }
}

val HeroPhoneStyle = CssStyle {
    base { Modifier.flexShrink(0) }
    Breakpoint.MD { Modifier.margin(left = (-6).cssRem) }
}

val GalleryStepCardStyle = CssStyle {
    base {
        Modifier
            .minHeight(19.cssRem)
            .padding(2.cssRem)
            .borderRadius(1.75.cssRem)
    }
    Breakpoint.MD { Modifier.minHeight(25.cssRem) }
}

private const val HERO_STAGE_FADE = "linear-gradient(#000 70%, transparent)"

val SplitSectionStyle = CssStyle.base {
    Modifier
        .fillMaxWidth()
        .gap(4.cssRem)
        .flexWrap(FlexWrap.Wrap)
}

val SplitMainStyle = CssStyle.base {
    Modifier
        .flexGrow(999)
        .flexShrink(1)
        .flexBasis(34.cssRem)
        .minWidth(0.px)
}

val SplitAsideStyle = CssStyle.base {
    Modifier
        .flexGrow(1)
        .flexShrink(1)
        .flexBasis(15.cssRem)
        .minWidth(0.px)
}

val OpinionTextStyle = CssStyle.base {
    Modifier
        .flexGrow(1)
        .flexShrink(1)
        .flexBasis(28.cssRem)
        .minWidth(0.px)
        .maxWidth(44.cssRem)
        .gap(0.5.cssRem)
}

val OpinionProofsStyle = CssStyle.base {
    Modifier
        .gap(0.5.cssRem)
        .flexWrap(FlexWrap.Wrap)
}

@Composable
fun BusinessHeroActions() {
    HeroActions {
        ButtonLink(
            SiteRes.strings.site_hero_guides_action.desc(),
            WebLocalization.localize(SiteNavigation.GUIDES_PATH),
            ButtonLinkVariant.FILLED,
        )
        ButtonLink(
            SiteRes.strings.site_hero_source_action.desc(),
            SiteNavigation.source.first().path,
            ButtonLinkVariant.TONAL,
        )
    }
    TextBlock(
        SiteRes.strings.site_stores_coming_soon.desc(),
        BodyMediumTextStyle
            .toModifier()
            .secondary(),
    )
}

@Composable
fun BusinessHeroVisual() {
    val label = SiteRes.strings.site_media_coming_soon.desc()
    Row(HeroStageStyle.toModifier(), verticalAlignment = Alignment.Top) {
        MediaPlaceholder(
            label,
            MediaFrame.BROWSER,
            Modifier
                .width(30.cssRem)
                .flexShrink(0)
                .margin(top = 4.cssRem)
                .displayIfAtLeast(Breakpoint.MD),
        )
        MediaPlaceholder(
            label,
            MediaFrame.PHONE,
            HeroPhoneStyle
                .toModifier()
                .width(18.5.cssRem),
        )
    }
}

@Composable
fun StepsSection() {
    FullBleedLandingBand(LandingSection.HOW, BandTone.ALTERNATE) {
        SectionContainer {
            SectionIntro(
                SiteRes.strings.site_how_title.desc(),
                SiteRes.strings.site_how_subtitle.desc(),
            )
        }
        CardGallery(
            BusinessContent.steps.size,
            SiteRes.strings.site_how_title.desc(),
            stepsNavigation(),
        ) { index, modifier ->
            val step = BusinessContent.steps[index]
            StepCard(
                index + 1,
                step.title,
                step.description,
                modifier.then(GalleryStepCardStyle.toModifier()),
            ) {
                StepSnippet(step.illustration)
            }
        }
    }
}

private fun stepsNavigation(): GalleryNavigation {
    return GalleryNavigation(
        SiteRes.strings.site_steps_previous.desc(),
        SiteRes.strings.site_steps_next.desc(),
        SiteRes.images.material_chevron_left,
        SiteRes.images.material_chevron_right,
    )
}

@Composable
private fun StepSnippet(illustration: StepIllustration) {
    when (illustration) {
        StepIllustration.SHARE_LINK -> ShareLinkSnippet()
        StepIllustration.TIME_SLOTS -> TimeSlotsSnippet()
        StepIllustration.APPROVE_REQUEST -> ApproveRequestSnippet()
    }
}

@Composable
fun FeaturesSection() {
    LandingBand(LandingSection.FEATURES) {
        Row(SplitSectionStyle.toModifier(), verticalAlignment = Alignment.CenterVertically) {
            Column(SplitMainStyle.toModifier()) {
                SectionIntro(SiteRes.strings.site_features_title.desc())
                FeatureGrid(BusinessContent.features, wideColumns = 2)
            }
            Box(SplitAsideStyle.toModifier(), contentAlignment = Alignment.Center) {
                MediaPlaceholder(SiteRes.strings.site_media_coming_soon.desc(), MediaFrame.PHONE)
            }
        }
    }
}

@Composable
fun PhilosophySection() {
    LandingBand(LandingSection.PHILOSOPHY) {
        SectionIntro(
            SiteRes.strings.site_philosophy_title.desc(),
            SiteRes.strings.site_philosophy_subtitle.desc(),
        )
        DividedRows {
            BusinessContent.opinions.forEach { OpinionRow(it) }
        }
    }
}

@Composable
private fun OpinionRow(opinion: Opinion) {
    DividedRow {
        Column(OpinionTextStyle.toModifier()) {
            Heading(opinion.title, HeadingLevel.H3, HeadlineSmallTextStyle.toModifier())
            TextBlock(
                opinion.description,
                BodyMediumTextStyle
                    .toModifier()
                    .secondary(),
            )
            TextBlock(
                opinion.marketplaceNote,
                LabelMediumTextStyle
                    .toModifier()
                    .secondary(),
            )
        }
        if (opinion.proofs.isNotEmpty()) {
            Row(OpinionProofsStyle.toModifier()) {
                opinion.proofs.forEach { ButtonLink(it.title, it.path, ButtonLinkVariant.TONAL) }
            }
        }
    }
}

@Composable
fun PrivacySection() {
    PrivacyPanel(SiteRes.strings.site_privacy_subtitle.desc(), BusinessContent.privacy) {
        TeamKeyDiagram(BusinessContent.keyHolders, it)
    }
}

@Composable
fun LaunchSection() {
    LandingBand(null) {
        Column(
            Modifier
                .fillMaxWidth()
                .gap(1.25.cssRem)
                .textAlign(TextAlign.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Heading(
                SiteRes.strings.site_stores_coming_soon.desc(),
                HeadingLevel.H2,
                HeadlineLargeTextStyle
                    .toModifier()
                    .maxWidth(40.cssRem),
            )
            TextBlock(
                SiteRes.strings.site_launch_subtitle.desc(),
                BodyLargeTextStyle
                    .toModifier()
                    .maxWidth(34.cssRem)
                    .secondary(),
            )
            Row(
                Modifier
                    .gap(0.75.cssRem)
                    .flexWrap(FlexWrap.Wrap)
                    .justifyContent(JustifyContent.Center),
            ) {
                ButtonLink(
                    SiteRes.strings.site_hero_guides_action.desc(),
                    WebLocalization.localize(SiteNavigation.GUIDES_PATH),
                    ButtonLinkVariant.FILLED,
                )
                ButtonLink(
                    SiteRes.strings.site_guides_contact_link.desc(),
                    WebLocalization.localize(SiteNavigation.CONTACT_PATH),
                    ButtonLinkVariant.TONAL,
                )
            }
        }
    }
}
