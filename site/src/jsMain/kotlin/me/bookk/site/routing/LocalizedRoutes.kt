package me.bookk.site.routing

import androidx.compose.runtime.Composable
import com.varabyte.kobweb.core.AppGlobals
import com.varabyte.kobweb.core.init.InitKobweb
import com.varabyte.kobweb.core.init.InitKobwebContext
import com.varabyte.kobweb.core.isExporting
import com.varabyte.kobweb.navigation.InitRouteMethod
import com.varabyte.kobweb.navigation.Router
import me.bookk.site.components.layouts.PAGE_LAYOUT_ID
import me.bookk.site.designsystem.resources.AppLanguage
import me.bookk.site.designsystem.resources.WebLocalization
import me.bookk.site.pages.ContactPage
import me.bookk.site.pages.DpaPage
import me.bookk.site.pages.GuidesPage
import me.bookk.site.pages.HomePage
import me.bookk.site.pages.PrivacyPage
import me.bookk.site.pages.TermsPage
import me.bookk.site.pages.dpa.DpaVersionPage
import me.bookk.site.pages.dpa.initDpaVersionPage
import me.bookk.site.pages.initContactPage
import me.bookk.site.pages.initDpaPage
import me.bookk.site.pages.initGuidesPage
import me.bookk.site.pages.initHomePage
import me.bookk.site.pages.initPrivacyPage
import me.bookk.site.pages.initTermsPage
import me.bookk.site.pages.privacy.PrivacyVersionPage
import me.bookk.site.pages.privacy.initPrivacyVersionPage
import me.bookk.site.pages.terms.TermsVersionPage
import me.bookk.site.pages.terms.initTermsVersionPage

private class LocalizedPage(
    val route: String,
    val initRoute: InitRouteMethod,
    val content: @Composable () -> Unit,
)

private val localizedPages = listOf(
    LocalizedPage("/", ::initHomePage) { HomePage() },
    LocalizedPage("/guides", ::initGuidesPage) { GuidesPage() },
    LocalizedPage("/contact", ::initContactPage) { ContactPage() },
    LocalizedPage("/privacy", ::initPrivacyPage) { PrivacyPage() },
    LocalizedPage("/privacy/{version}", ::initPrivacyVersionPage) { PrivacyVersionPage() },
    LocalizedPage("/terms", ::initTermsPage) { TermsPage() },
    LocalizedPage("/terms/{version}", ::initTermsVersionPage) { TermsVersionPage() },
    LocalizedPage("/dpa", ::initDpaPage) { DpaPage() },
    LocalizedPage("/dpa/{version}", ::initDpaVersionPage) { DpaVersionPage() },
)

internal val localizedPageRoutes: List<String>
    get() = localizedPages.map { it.route }

@InitKobweb
fun initLocalizedRoutes(ctx: InitKobwebContext) {
    AppLanguage.entries
        .filter { it != AppLanguage.DEFAULT }
        .forEach { ctx.router.registerPages(it) }
    ctx.router.addRouteInterceptor {
        path = LanguageRedirect.target(path, preferredLanguage())
    }
}

private fun Router.registerPages(language: AppLanguage) {
    localizedPages.forEach { page ->
        register(language.localize(page.route), PAGE_LAYOUT_ID, page.initRoute) { page.content() }
    }
}

private fun preferredLanguage(): AppLanguage {
    if (AppGlobals.isExporting) return AppLanguage.DEFAULT
    return WebLocalization.preferredLanguage()
}
