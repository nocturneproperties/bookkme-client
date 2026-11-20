package me.bookk.site.layout

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.compose.css.AlignItems
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.ColumnScope
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.alignItems
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.flexGrow
import com.varabyte.kobweb.compose.ui.modifiers.maxWidth
import com.varabyte.kobweb.compose.ui.modifiers.minHeight
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.core.PageContext
import com.varabyte.kobweb.core.data.getValue
import com.varabyte.kobweb.core.init.InitRoute
import com.varabyte.kobweb.core.init.InitRouteContext
import com.varabyte.kobweb.core.layout.Layout
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.base
import com.varabyte.kobweb.silk.style.toModifier
import com.varabyte.kobweb.silk.theme.colors.ColorMode
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import me.bookk.site.designsystem.components.ContentMaxWidth
import me.bookk.site.designsystem.head.HeadTags
import me.bookk.site.designsystem.head.LanguageLinks
import me.bookk.site.designsystem.head.PageMeta
import me.bookk.site.designsystem.head.PageMetaTags
import me.bookk.site.designsystem.resources.AppLanguage
import me.bookk.site.designsystem.resources.WebLocalization
import me.bookk.site.designsystem.resources.localized
import me.bookk.site.designsystem.theme.BandTone
import me.bookk.site.designsystem.theme.surfaceColors
import me.bookk.site.designsystem.theme.surfaces
import me.bookk.site.designsystem.theme.webColors
import me.bookk.site.navigation.SiteNavigation
import me.bookk.site.resources.SiteRes
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.vh

const val PAGE_LAYOUT_ID = "me.bookk.site.layout.PageLayout"

val PageContentStyle = CssStyle.base {
    Modifier
        .fillMaxWidth()
        .maxWidth(ContentMaxWidth)
        .padding(leftRight = 1.cssRem, bottom = 4.cssRem)
}

val FullWidthPageContentStyle = CssStyle.base {
    Modifier
        .fillMaxWidth()
        .alignItems(AlignItems.Center)
}

class PageLayoutData(
    val title: StringDesc,
    val description: StringDesc = SiteRes.strings.site_description.desc(),
    val isIndexed: Boolean = true,
    val isFullWidth: Boolean = false,
) {
    companion object {
        fun notFound(): PageLayoutData {
            return PageLayoutData(SiteRes.strings.site_not_found_title.desc(), isIndexed = false)
        }
    }
}

@InitRoute
fun initPageLayout(ctx: InitRouteContext) {
    WebLocalization.show(AppLanguage.fromPath(ctx.route.path) ?: AppLanguage.DEFAULT)
}

@Composable
@Layout
fun PageLayout(ctx: PageContext, content: @Composable ColumnScope.() -> Unit) {
    val data: PageLayoutData = ctx.data.getValue()
    val contentStyle = if (data.isFullWidth) FullWidthPageContentStyle else PageContentStyle
    PageHead(data, ctx.route.path)
    Column(
        Modifier
            .fillMaxWidth()
            .minHeight(100.vh)
            .surfaceColors(BandTone.PLAIN.surfaces(), ColorMode.current.webColors()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        NavHeader()
        Column(
            contentStyle
                .toModifier()
                .flexGrow(1),
        ) {
            content()
        }
        Footer(Modifier.fillMaxWidth())
    }
}

@Composable
internal fun PageHead(data: PageLayoutData, path: String) {
    val title = data.title.localized()
    val page = PageMeta(path, title, data.description.localized(), data.isIndexed)
    val links = if (data.isIndexed) {
        LanguageLinks.forPath(
            SiteNavigation.ORIGIN,
            path,
        )
    } else {
        emptyList()
    }
    HeadTags(
        "$title · ${SiteRes.strings.site_app_name.desc().localized()}",
        PageMetaTags.forPage(SiteNavigation.ORIGIN, page),
        links,
    )
}
