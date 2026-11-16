package me.bookk.site.pages

import com.varabyte.kobweb.core.data.add
import com.varabyte.kobweb.core.init.InitKobweb
import com.varabyte.kobweb.core.init.InitKobwebContext
import com.varabyte.kobweb.core.init.InitRouteContext
import dev.icerock.moko.resources.desc.desc
import me.bookk.site.components.layouts.PageLayoutData
import me.bookk.site.components.sections.NotFoundMessage
import me.bookk.site.resources.SiteRes

private const val PAGE_LAYOUT_ID = "me.bookk.site.components.layouts.PageLayout"

@InitKobweb
fun initNotFoundPage(ctx: InitKobwebContext) {
    ctx.router.setErrorPage(PAGE_LAYOUT_ID, ::initNotFoundRoute) {
        NotFoundMessage()
    }
}

private fun initNotFoundRoute(ctx: InitRouteContext) {
    ctx.data.add(PageLayoutData(SiteRes.strings.site_not_found_title.desc()))
}
