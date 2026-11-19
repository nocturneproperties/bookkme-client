package me.bookk.site.components.sections

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.varabyte.kobweb.compose.foundation.layout.Row
import com.varabyte.kobweb.compose.foundation.layout.Spacer
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxHeight
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.gap
import com.varabyte.kobweb.compose.ui.modifiers.height
import com.varabyte.kobweb.compose.ui.modifiers.maxWidth
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.core.rememberPageContext
import com.varabyte.kobweb.silk.components.navigation.Link
import com.varabyte.kobweb.silk.components.navigation.UncoloredLinkVariant
import com.varabyte.kobweb.silk.components.navigation.UndecoratedLinkVariant
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.base
import com.varabyte.kobweb.silk.style.breakpoint.Breakpoint
import com.varabyte.kobweb.silk.style.breakpoint.displayIfAtLeast
import com.varabyte.kobweb.silk.style.breakpoint.displayUntil
import com.varabyte.kobweb.silk.style.toModifier
import dev.icerock.moko.resources.desc.desc
import me.bookk.site.components.widgets.Logo
import me.bookk.site.content.SiteNavigation
import me.bookk.site.designsystem.components.ColorModeButton
import me.bookk.site.designsystem.components.ContentMaxWidth
import me.bookk.site.designsystem.components.LanguagePicker
import me.bookk.site.designsystem.components.NavigationBar
import me.bookk.site.designsystem.components.NavigationMenuButton
import me.bookk.site.designsystem.components.SegmentedLinkItem
import me.bookk.site.designsystem.components.SegmentedLinks
import me.bookk.site.designsystem.components.TextLink
import me.bookk.site.designsystem.resources.WebLocalization
import me.bookk.site.designsystem.theme.LabelMediumTextStyle
import me.bookk.site.designsystem.theme.TitleMediumTextStyle
import me.bookk.site.resources.SiteRes
import org.jetbrains.compose.web.css.cssRem

private val CompactNavigationBreakpoint = Breakpoint.MD

val NavBarStyle = CssStyle.base {
    Modifier
        .fillMaxWidth()
        .maxWidth(ContentMaxWidth)
        .fillMaxHeight()
        .padding(leftRight = 1.cssRem)
        .gap(2.cssRem)
}

@Composable
fun NavHeader() {
    var isMenuExpanded by remember { mutableStateOf(false) }
    NavigationBar(
        height = 3.cssRem,
        isExpanded = isMenuExpanded,
        onCollapse = { isMenuExpanded = false },
        menuBreakpoint = CompactNavigationBreakpoint,
        bar = {
            Row(NavBarStyle.toModifier(), verticalAlignment = Alignment.CenterVertically) {
                HomeLink()
                AudienceSwitch()
                SectionLinks()
                Spacer()
                LanguagePicker(SiteRes.strings.site_language.desc())
                ColorModeButton(SiteRes.strings.site_toggle_color_mode.desc())
                NavigationMenuButton(
                    title = SiteRes.strings.site_menu.desc(),
                    isExpanded = isMenuExpanded,
                    onClick = { isMenuExpanded = !isMenuExpanded },
                    modifier = Modifier.displayUntil(CompactNavigationBreakpoint),
                )
            }
        },
        menu = {
            (SiteNavigation.audiences + SiteNavigation.header).forEach {
                TextLink(
                    it.title,
                    WebLocalization.localize(it.path),
                    TitleMediumTextStyle.toModifier(),
                )
            }
        },
    )
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
private fun AudienceSwitch() {
    val path = rememberPageContext().route.path
    val items = SiteNavigation.audiences.map {
        val localizedPath = WebLocalization.localize(it.path)
        SegmentedLinkItem(it.title, localizedPath, localizedPath == path)
    }
    SegmentedLinks(
        SiteRes.strings.site_nav_audience.desc(),
        items,
        Modifier.displayIfAtLeast(CompactNavigationBreakpoint),
    )
}

@Composable
private fun SectionLinks() {
    Row(
        Modifier
            .gap(1.5.cssRem)
            .displayIfAtLeast(CompactNavigationBreakpoint),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SiteNavigation.header.forEach {
            TextLink(it.title, WebLocalization.localize(it.path), LabelMediumTextStyle.toModifier())
        }
    }
}
