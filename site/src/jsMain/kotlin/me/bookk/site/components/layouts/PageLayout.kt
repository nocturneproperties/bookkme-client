package me.bookk.site.components.layouts

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.varabyte.kobweb.compose.foundation.layout.Column
import com.varabyte.kobweb.compose.foundation.layout.ColumnScope
import com.varabyte.kobweb.compose.ui.Alignment
import com.varabyte.kobweb.compose.ui.Modifier
import com.varabyte.kobweb.compose.ui.modifiers.fillMaxWidth
import com.varabyte.kobweb.compose.ui.modifiers.flexGrow
import com.varabyte.kobweb.compose.ui.modifiers.maxWidth
import com.varabyte.kobweb.compose.ui.modifiers.minHeight
import com.varabyte.kobweb.compose.ui.modifiers.padding
import com.varabyte.kobweb.core.PageContext
import com.varabyte.kobweb.core.data.getValue
import com.varabyte.kobweb.core.layout.Layout
import com.varabyte.kobweb.silk.style.CssStyle
import com.varabyte.kobweb.silk.style.base
import com.varabyte.kobweb.silk.style.toModifier
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import kotlinx.browser.document
import me.bookk.site.components.sections.Footer
import me.bookk.site.components.sections.NavHeader
import me.bookk.site.designsystem.resources.localized
import me.bookk.site.resources.SiteRes
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.vh

val PageContentStyle = CssStyle.base {
    Modifier
        .fillMaxWidth()
        .maxWidth(64.cssRem)
        .padding(leftRight = 1.cssRem, bottom = 4.cssRem)
}

class PageLayoutData(
    val title: StringDesc,
)

@Composable
@Layout
fun PageLayout(ctx: PageContext, content: @Composable ColumnScope.() -> Unit) {
    val pageTitle = ctx.data.getValue<PageLayoutData>().title.localized()
    val title = "$pageTitle · ${SiteRes.strings.site_app_name.desc().localized()}"
    LaunchedEffect(title) {
        document.title = title
    }
    Column(
        Modifier
            .fillMaxWidth()
            .minHeight(100.vh),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        NavHeader()
        Column(
            PageContentStyle
                .toModifier()
                .flexGrow(1),
        ) {
            content()
        }
        Footer(Modifier.fillMaxWidth())
    }
}
