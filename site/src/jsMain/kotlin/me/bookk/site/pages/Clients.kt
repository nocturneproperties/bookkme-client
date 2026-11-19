package me.bookk.site.pages

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.data.add
import com.varabyte.kobweb.core.init.InitRoute
import com.varabyte.kobweb.core.init.InitRouteContext
import com.varabyte.kobweb.core.layout.Layout
import dev.icerock.moko.resources.desc.desc
import me.bookk.site.components.layouts.PageLayoutData
import me.bookk.site.components.sections.BookingHistorySection
import me.bookk.site.components.sections.BookingWaysSection
import me.bookk.site.components.sections.CenteredHero
import me.bookk.site.components.sections.ClientPrivacySection
import me.bookk.site.components.sections.ClientPromisesSection
import me.bookk.site.components.sections.LeavingStatement
import me.bookk.site.resources.SiteRes

@InitRoute
fun initClientsPage(ctx: InitRouteContext) {
    ctx.data.add(
        PageLayoutData(
            SiteRes.strings.site_clients_page_title.desc(),
            SiteRes.strings.site_clients_description.desc(),
            isFullWidth = true,
        ),
    )
}

@Page
@Layout(".components.layouts.PageLayout")
@Composable
fun ClientsPage() {
    CenteredHero(
        SiteRes.strings.site_clients_headline.desc(),
        SiteRes.strings.site_clients_subheadline.desc(),
    )
    BookingWaysSection()
    ClientPromisesSection()
    BookingHistorySection()
    ClientPrivacySection()
    LeavingStatement()
}
