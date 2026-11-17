package me.bookk.site.pages

import com.varabyte.kobweb.core.data.add
import com.varabyte.kobweb.core.init.InitKobweb
import com.varabyte.kobweb.core.init.InitKobwebContext
import com.varabyte.kobweb.core.init.InitRouteContext
import me.bookk.site.components.layouts.PAGE_LAYOUT_ID
import me.bookk.site.components.layouts.PageLayoutData
import me.bookk.site.components.sections.NotFoundMessage

@InitKobweb
fun initNotFoundPage(ctx: InitKobwebContext) {
    ctx.router.setErrorPage(PAGE_LAYOUT_ID, ::initNotFoundRoute) {
        NotFoundMessage()
    }
}

private fun initNotFoundRoute(ctx: InitRouteContext) {
    ctx.data.add(PageLayoutData.notFound())
}
