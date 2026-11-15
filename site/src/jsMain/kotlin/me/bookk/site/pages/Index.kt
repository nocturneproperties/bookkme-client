package me.bookk.site.pages

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.color
import com.varabyte.kobweb.compose.ui.modifiers.flexWrap
import com.varabyte.kobweb.compose.ui.modifiers.gap
import com.varabyte.kobweb.compose.ui.modifiers.height
import com.varabyte.kobweb.compose.ui.modifiers.id
import com.varabyte.kobweb.compose.ui.modifiers.margin
import com.varabyte.kobweb.compose.ui.modifiers.maxWidth
import com.varabyte.kobweb.compose.ui.modifiers.scrollMarginBlock
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.data.add
import com.varabyte.kobweb.core.init.InitRoute
import com.varabyte.kobweb.core.init.InitRouteContext
import com.varabyte.kobweb.core.layout.Layout
import com.varabyte.kobweb.silk.components.layout.SimpleGrid
import com.varabyte.kobweb.silk.components.layout.numColumns
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.base
import com.varabyte.kobweb.silk.style.breakpoint.Breakpoint
import com.varabyte.kobweb.silk.style.toModifier
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import me.bookk.designsystem.resources.color.ColorToken
import me.bookk.site.components.layouts.PageLayoutData
import me.bookk.site.components.widgets.FeatureCard
import me.bookk.site.components.widgets.Logo
import me.bookk.site.components.widgets.StoreBadge
import me.bookk.site.content.Feature
import me.bookk.site.content.LandingContent
import me.bookk.site.content.LandingSection
import me.bookk.site.designsystem.components.Heading
import me.bookk.site.designsystem.components.HeadingLevel
import me.bookk.site.designsystem.components.TextBlock
import me.bookk.site.designsystem.theme.BodyLargeTextStyle
import me.bookk.site.designsystem.theme.BodyMediumTextStyle
import me.bookk.site.designsystem.theme.DisplayTextStyle
import me.bookk.site.designsystem.theme.HeadlineLargeTextStyle
import me.bookk.site.designsystem.theme.themed
import me.bookk.site.resources.SiteRes
import org.jetbrains.compose.web.css.FlexWrap
import org.jetbrains.compose.web.css.cssRem

val HeroStyle = CssStyle {
    base {
        Modifier
            .gap(1.5.cssRem)
            .margin(top = 3.cssRem, bottom = 4.cssRem)
    }
    Breakpoint.MD { Modifier.margin(top = 6.cssRem, bottom = 6.cssRem) }
}

val SectionStyle = CssStyle.base {
    Modifier
        .gap(1.5.cssRem)
        .margin(bottom = 4.cssRem)
        .scrollMarginBlock(start = 4.cssRem)
}

@InitRoute
fun initHomePage(ctx: InitRouteContext) {
    ctx.data.add(PageLayoutData(SiteRes.strings.site_page_title.desc()))
}

@Page
@Layout(".components.layouts.PageLayout")
@Composable
fun HomePage() {
    Hero()
    FeatureSection(
        LandingSection.FEATURES,
        SiteRes.strings.site_features_title.desc(),
        null,
        LandingContent.features,
        wideColumns = 3,
    )
    FeatureSection(
        LandingSection.PRIVACY,
        SiteRes.strings.site_privacy_title.desc(),
        SiteRes.strings.site_privacy_subtitle.desc(),
        LandingContent.privacy,
        wideColumns = 2,
    )
    SecondaryText(SiteRes.strings.site_languages.desc())
}

@Composable
private fun Hero() {
    Column(HeroStyle.toModifier()) {
        Logo(Modifier.height(4.cssRem))
        Heading(
            SiteRes.strings.site_headline.desc(),
            HeadingLevel.H1,
            DisplayTextStyle
                .toModifier()
                .maxWidth(48.cssRem),
        )
        TextBlock(
            SiteRes.strings.site_subheadline.desc(),
            BodyLargeTextStyle
                .toModifier()
                .maxWidth(44.cssRem)
                .color(ColorToken.SecondaryText.themed),
        )
        StoreBadges()
    }
}

@Composable
private fun StoreBadges() {
    Column(Modifier.gap(0.75.cssRem)) {
        SecondaryText(SiteRes.strings.site_stores_title.desc())
        Row(
            Modifier
                .gap(0.75.cssRem)
                .flexWrap(FlexWrap.Wrap),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            StoreBadge(SiteRes.strings.site_app_store.desc())
            StoreBadge(SiteRes.strings.site_google_play.desc())
        }
    }
}

@Composable
private fun FeatureSection(
    section: LandingSection,
    title: StringDesc,
    subtitle: StringDesc?,
    features: List<Feature>,
    wideColumns: Int,
) {
    Column(
        SectionStyle
            .toModifier()
            .id(section.id),
    ) {
        Heading(title, HeadingLevel.H2, HeadlineLargeTextStyle.toModifier())
        subtitle?.let {
            TextBlock(
                it,
                BodyLargeTextStyle
                    .toModifier()
                    .maxWidth(44.cssRem)
                    .color(ColorToken.SecondaryText.themed),
            )
        }
        SimpleGrid(numColumns(1, md = 2, lg = wideColumns), Modifier.gap(1.cssRem)) {
            features.forEach { FeatureCard(it) }
        }
    }
}

@Composable
private fun SecondaryText(text: StringDesc) {
    TextBlock(
        text,
        BodyMediumTextStyle
            .toModifier()
            .color(ColorToken.SecondaryText.themed),
    )
}
