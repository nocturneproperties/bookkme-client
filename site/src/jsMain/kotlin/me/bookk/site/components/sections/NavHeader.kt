package me.bookk.site.components.sections

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.functions.blur
import com.varabyte.kobweb.compose.css.functions.saturate
import com.varabyte.kobweb.compose.foundation.layout.Box
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.foundation.layout.Spacer
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.backdropFilter
import com.varabyte.kobweb.compose.ui.modifiers.backgroundColor
import com.varabyte.kobweb.compose.ui.modifiers.borderBottom
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.gap
import com.varabyte.kobweb.compose.ui.modifiers.height
import com.varabyte.kobweb.compose.ui.modifiers.maxWidth
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.compose.ui.modifiers.position
import com.varabyte.kobweb.compose.ui.modifiers.top
import com.varabyte.kobweb.compose.ui.modifiers.zIndex
import com.varabyte.kobweb.silk.components.navigation.Link
import com.varabyte.kobweb.silk.components.navigation.UncoloredLinkVariant
import com.varabyte.kobweb.silk.components.navigation.UndecoratedLinkVariant
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.base
import com.varabyte.kobweb.silk.style.breakpoint.Breakpoint
import com.varabyte.kobweb.silk.style.breakpoint.displayIfAtLeast
import com.varabyte.kobweb.silk.style.toModifier
import dev.icerock.moko.resources.desc.desc
import me.bookk.designsystem.resources.color.ColorToken
import me.bookk.site.components.widgets.Logo
import me.bookk.site.content.SiteNavigation
import me.bookk.site.designsystem.components.ColorModeButton
import me.bookk.site.designsystem.components.LanguagePicker
import me.bookk.site.designsystem.components.TextLink
import me.bookk.site.designsystem.resources.WebLocalization
import me.bookk.site.designsystem.theme.LabelMediumTextStyle
import me.bookk.site.designsystem.theme.resolve
import me.bookk.site.designsystem.theme.webColors
import me.bookk.site.resources.SiteRes
import org.jetbrains.compose.web.css.LineStyle
import org.jetbrains.compose.web.css.Position
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.px

val NavHeaderStyle = CssStyle.base {
    val colors = colorMode.webColors()
    Modifier
        .fillMaxWidth()
        .position(Position.Sticky)
        .top(0.px)
        .zIndex(1)
        .backgroundColor(
            ColorToken.Elevated
                .resolve(colors)
                .toRgb()
                .copyf(alpha = 0.75f),
        )
        .backdropFilter(saturate(180.percent), blur(20.px))
        .borderBottom(1.px, LineStyle.Solid, ColorToken.Divider.resolve(colors))
}

val NavBarStyle = CssStyle.base {
    Modifier
        .fillMaxWidth()
        .maxWidth(64.cssRem)
        .height(3.cssRem)
        .padding(leftRight = 1.cssRem)
        .gap(2.cssRem)
}

@Composable
fun NavHeader() {
    Box(NavHeaderStyle.toModifier(), contentAlignment = Alignment.Center) {
        Row(NavBarStyle.toModifier(), verticalAlignment = Alignment.CenterVertically) {
            HomeLink()
            SectionLinks()
            Spacer()
            LanguagePicker(SiteRes.strings.site_language.desc())
            ColorModeButton(SiteRes.strings.site_toggle_color_mode.desc())
        }
    }
}

@Composable
private fun HomeLink() {
    Link(
        WebLocalization.localize("/"),
        variant = UndecoratedLinkVariant.then(UncoloredLinkVariant),
    ) {
        Logo(Modifier.height(1.5.cssRem))
    }
}

@Composable
private fun SectionLinks() {
    Row(
        Modifier
            .gap(1.5.cssRem)
            .displayIfAtLeast(Breakpoint.MD),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SiteNavigation.header.forEach {
            TextLink(it.title, WebLocalization.localize(it.path), LabelMediumTextStyle.toModifier())
        }
    }
}
