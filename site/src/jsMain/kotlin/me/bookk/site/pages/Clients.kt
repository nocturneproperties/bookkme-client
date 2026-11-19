package me.bookk.site.pages

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.data.add
import com.varabyte.kobweb.core.init.InitRoute
import com.varabyte.kobweb.core.init.InitRouteContext
import com.varabyte.kobweb.core.layout.Layout
import dev.icerock.moko.resources.desc.desc
import me.bookk.site.landing.CenteredHero
import me.bookk.site.landing.clients.BookingHistorySection
import me.bookk.site.landing.clients.BookingWaysSection
import me.bookk.site.landing.clients.ClientPrivacySection
import me.bookk.site.landing.clients.ClientPromisesSection
import me.bookk.site.landing.clients.LeavingStatement
import me.bookk.site.layout.PageLayoutData
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
@Layout(".layout.PageLayout")
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
