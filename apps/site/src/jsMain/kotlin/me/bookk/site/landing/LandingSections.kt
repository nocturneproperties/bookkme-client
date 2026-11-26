package me.bookk.site.landing

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.JustifyContent
import com.varabyte.kobweb.compose.css.TextAlign
import com.varabyte.kobweb.compose.css.WhiteSpace
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.ColumnScope
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.flexBasis
import com.varabyte.kobweb.compose.ui.modifiers.flexWrap
import com.varabyte.kobweb.compose.ui.modifiers.gap
import com.varabyte.kobweb.compose.ui.modifiers.id
import com.varabyte.kobweb.compose.ui.modifiers.justifyContent
import com.varabyte.kobweb.compose.ui.modifiers.margin
import com.varabyte.kobweb.compose.ui.modifiers.maxWidth
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.compose.ui.modifiers.scrollMarginBlock
import com.varabyte.kobweb.compose.ui.modifiers.textAlign
import com.varabyte.kobweb.compose.ui.modifiers.whiteSpace
import com.varabyte.kobweb.silk.components.layout.SimpleGrid
import com.varabyte.kobweb.silk.components.layout.numColumns
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.base
import com.varabyte.kobweb.silk.style.breakpoint.Breakpoint
import com.varabyte.kobweb.silk.style.toModifier
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import me.bookk.designsystem.components.AccentHeading
import me.bookk.designsystem.components.AccentTitledBlock
import me.bookk.designsystem.components.Band
import me.bookk.designsystem.components.BandSurface
import me.bookk.designsystem.components.Heading
import me.bookk.designsystem.components.HeadingLevel
import me.bookk.designsystem.components.SectionContainer
import me.bookk.designsystem.components.TextBlock
import me.bookk.designsystem.components.TextLink
import me.bookk.designsystem.resources.WebLocalization
import me.bookk.designsystem.theme.BandTone
import me.bookk.designsystem.theme.BodyLargeTextStyle
import me.bookk.designsystem.theme.DisplayTextStyle
import me.bookk.designsystem.theme.HeadlineLargeTextStyle
import me.bookk.designsystem.theme.LeadTextStyle
import me.bookk.designsystem.theme.StatementTextStyle
import me.bookk.designsystem.theme.TitleMediumTextStyle
import me.bookk.designsystem.theme.active
import me.bookk.designsystem.theme.secondary
import me.bookk.site.landing.business.SplitAsideStyle
import me.bookk.site.landing.business.SplitMainStyle
import me.bookk.site.landing.business.SplitSectionStyle
import me.bookk.site.legal.LegalDocument
import me.bookk.site.resources.SiteRes
import org.jetbrains.compose.web.css.FlexWrap
import org.jetbrains.compose.web.css.cssRem

val HeroStyle = CssStyle {
    base {
        Modifier
            .fillMaxWidth()
            .gap(1.25.cssRem)
            .padding(top = 3.cssRem, bottom = 4.cssRem)
    }
    Breakpoint.MD { Modifier.padding(top = 6.cssRem, bottom = 6.cssRem) }
}

val ProductHeroStyle = CssStyle {
    base {
        Modifier
            .fillMaxWidth()
            .gap(1.25.cssRem)
            .padding(top = 3.cssRem)
    }
    Breakpoint.MD { Modifier.padding(top = 6.cssRem) }
}

val SectionIntroStyle = CssStyle.base {
    Modifier
        .maxWidth(40.cssRem)
        .gap(0.75.cssRem)
        .margin(bottom = 3.cssRem)
}

val FeatureGridStyle = CssStyle.base {
    Modifier.gap(2.5.cssRem)
}

val StatementStyle = CssStyle.base {
    Modifier.gap(1.5.cssRem)
}

@Composable
fun CenteredHero(
    headline: StringDesc,
    subheadline: StringDesc,
    visual: (@Composable () -> Unit)? = null,
    actions: @Composable () -> Unit = {},
) {
    val style = if (visual == null) HeroStyle else ProductHeroStyle
    SectionContainer {
        Column(
            style
                .toModifier()
                .textAlign(TextAlign.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            HeroText(headline, subheadline)
            actions()
            visual?.invoke()
        }
    }
}

@Composable
private fun HeroText(headline: StringDesc, subheadline: StringDesc) {
    Heading(
        headline,
        HeadingLevel.H1,
        DisplayTextStyle
            .toModifier()
            .maxWidth(48.cssRem),
    )
    TextBlock(
        subheadline,
        LeadTextStyle
            .toModifier()
            .maxWidth(40.cssRem)
            .whiteSpace(WhiteSpace.PreLine)
            .secondary(),
    )
}

@Composable
fun HeroActions(content: @Composable () -> Unit) {
    Row(
        Modifier
            .gap(0.75.cssRem)
            .flexWrap(FlexWrap.Wrap)
            .justifyContent(JustifyContent.Center),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        content()
    }
}

@Composable
fun SectionIntro(title: StringDesc, subtitle: StringDesc? = null) {
    Column(SectionIntroStyle.toModifier()) {
        Heading(title, HeadingLevel.H2, HeadlineLargeTextStyle.toModifier())
        subtitle?.let {
            TextBlock(
                it,
                LeadTextStyle
                    .toModifier()
                    .secondary(),
            )
        }
    }
}

@Composable
fun LandingBand(
    section: LandingSection?,
    tone: BandTone = BandTone.PLAIN,
    content: @Composable ColumnScope.() -> Unit,
) {
    Band(section.anchor(), tone, content = content)
}

@Composable
fun FullBleedLandingBand(
    section: LandingSection?,
    tone: BandTone = BandTone.PLAIN,
    content: @Composable ColumnScope.() -> Unit,
) {
    BandSurface(section.anchor(), tone, content = content)
}

private fun LandingSection?.anchor(): Modifier {
    return this?.let {
        Modifier
            .id(it.id)
            .scrollMarginBlock(start = 3.cssRem)
    } ?: Modifier
}

@Composable
fun FeatureGrid(features: List<Feature>, wideColumns: Int) {
    SimpleGrid(
        numColumns(1, md = 2, lg = wideColumns),
        FeatureGridStyle.toModifier(),
    ) {
        features.forEach { FeatureItem(it) }
    }
}

@Composable
private fun FeatureItem(feature: Feature) {
    AccentTitledBlock(feature.title) {
        TextBlock(
            feature.description,
            BodyLargeTextStyle
                .toModifier()
                .secondary(),
        )
    }
}

@Composable
fun Statement(title: StringDesc, accent: StringDesc, description: StringDesc) {
    LandingBand(null, BandTone.DARK) {
        Column(StatementStyle.toModifier()) {
            StatementTitle(title, accent)
            TextBlock(
                description,
                LeadTextStyle
                    .toModifier()
                    .maxWidth(37.5.cssRem)
                    .secondary(),
            )
        }
    }
}

@Composable
private fun StatementTitle(title: StringDesc, accent: StringDesc) {
    AccentHeading(
        title,
        accent,
        HeadingLevel.H2,
        StatementTextStyle
            .toModifier()
            .maxWidth(56.cssRem),
    )
}

@Composable
fun PrivacyPanel(
    subtitle: StringDesc,
    points: List<Feature>,
    diagram: @Composable (Modifier) -> Unit,
) {
    LandingBand(LandingSection.PRIVACY, BandTone.ALTERNATE) {
        SectionIntro(SiteRes.strings.site_privacy_title.desc(), subtitle)
        Row(SplitSectionStyle.toModifier(), verticalAlignment = Alignment.Top) {
            diagram(SplitMainStyle.toModifier())
            Column(
                SplitAsideStyle
                    .toModifier()
                    .flexBasis(18.cssRem)
                    .gap(2.cssRem),
            ) {
                points.forEach { PrivacyPoint(it) }
                TextLink(
                    SiteRes.strings.site_privacy_policy_link.desc(),
                    WebLocalization.localize(LegalDocument.PRIVACY_POLICY.path),
                    BodyLargeTextStyle
                        .toModifier()
                        .active(),
                )
            }
        }
    }
}

@Composable
private fun PrivacyPoint(point: Feature) {
    Column(Modifier.gap(0.375.cssRem)) {
        Heading(point.title, HeadingLevel.H3, TitleMediumTextStyle.toModifier())
        TextBlock(
            point.description,
            BodyLargeTextStyle
                .toModifier()
                .secondary(),
        )
    }
}
