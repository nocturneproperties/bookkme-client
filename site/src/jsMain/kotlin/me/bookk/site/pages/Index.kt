package me.bookk.site.pages

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.core.Page
import com.varabyte.kobweb.core.data.add
import com.varabyte.kobweb.core.init.InitRoute
import com.varabyte.kobweb.core.init.InitRouteContext
import com.varabyte.kobweb.core.layout.Layout
import dev.icerock.moko.resources.desc.desc
import me.bookk.site.components.layouts.PageLayoutData
import me.bookk.site.components.sections.BusinessHeroActions
import me.bookk.site.components.sections.BusinessHeroVisual
import me.bookk.site.components.sections.CenteredHero
import me.bookk.site.components.sections.FeaturesSection
import me.bookk.site.components.sections.LaunchSection
import me.bookk.site.components.sections.PhilosophySection
import me.bookk.site.components.sections.PrivacySection
import me.bookk.site.components.sections.Statement
import me.bookk.site.components.sections.StepsSection
import me.bookk.site.resources.SiteRes

@InitRoute
fun initHomePage(ctx: InitRouteContext) {
    ctx.data.add(PageLayoutData(SiteRes.strings.site_page_title.desc(), isFullWidth = true))
}

@Page
@Layout(".components.layouts.PageLayout")
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
