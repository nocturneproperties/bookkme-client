package me.bookk.site.pages

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.data.add
import com.varabyte.kobweb.core.init.InitRoute
import com.varabyte.kobweb.core.init.InitRouteContext
import com.varabyte.kobweb.core.layout.Layout
import dev.icerock.moko.resources.desc.desc
import me.bookk.site.landing.CenteredHero
import me.bookk.site.landing.Statement
import me.bookk.site.landing.business.BusinessHeroActions
import me.bookk.site.landing.business.BusinessHeroVisual
import me.bookk.site.landing.business.FeaturesSection
import me.bookk.site.landing.business.LaunchSection
import me.bookk.site.landing.business.PhilosophySection
import me.bookk.site.landing.business.PrivacySection
import me.bookk.site.landing.business.StepsSection
import me.bookk.site.layout.PageLayoutData
import me.bookk.site.resources.SiteRes

@InitRoute
fun initHomePage(ctx: InitRouteContext) {
    ctx.data.add(PageLayoutData(SiteRes.strings.site_page_title.desc(), isFullWidth = true))
}

@Page
@Layout(".layout.PageLayout")
@Composable
fun HomePage() {
    CenteredHero(
        SiteRes.strings.site_headline.desc(),
        SiteRes.strings.site_subheadline.desc(),
        visual = { BusinessHeroVisual() },
    ) {
        BusinessHeroActions()
    }
    StepsSection()
    FeaturesSection()
    Statement(
        SiteRes.strings.site_philosophy_clients_title_start.desc(),
        SiteRes.strings.site_philosophy_clients_title_accent.desc(),
        SiteRes.strings.site_philosophy_clients_description.desc(),
    )
    PhilosophySection()
    PrivacySection()
    LaunchSection()
}
