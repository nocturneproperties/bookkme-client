package build_src.site

import kotlin.test.Test
import kotlin.test.assertEquals

class SitePagesTest {
    private fun pages(
        pagePaths: List<String> = listOf("/", "/contact"),
        versionedPaths: List<String> = emptyList(),
    ): SitePages {
        return SitePages(
            pagePaths = pagePaths,
            versionedPaths = versionedPaths,
            defaultLanguage = "en",
            otherLanguages = listOf("uk", "pl"),
        )
    }

    @Test
    fun `keeps default language paths unprefixed`() {
        val pages = pages()

        val urls = pages.localized().map { it.urls.getValue("en") }

        assertEquals(listOf("/", "/contact"), urls)
    }

    @Test
    fun `prefixes other languages and drops the trailing slash of the home page`() {
        val pages = pages()

        val urls = pages.localized().map { it.urls }

        assertEquals(
            listOf(
                mapOf("en" to "/", "uk" to "/uk", "pl" to "/pl"),
                mapOf("en" to "/contact", "uk" to "/uk/contact", "pl" to "/pl/contact"),
            ),
            urls,
        )
    }

    @Test
    fun `lists versioned pages after the file routed pages`() {
        val pages = pages(versionedPaths = listOf("/privacy/2026-12-01"))

        val paths = pages.localized().map { it.urls.getValue("en") }

        assertEquals(listOf("/", "/contact", "/privacy/2026-12-01"), paths)
    }

    @Test
    fun `exports every url except the default language file routed pages`() {
        val pages = pages(
            pagePaths = listOf("/", "/privacy"),
            versionedPaths = listOf("/privacy/2026-12-01"),
        )

        val routes = pages.extraExportRoutes()

        assertEquals(
            listOf(
                "/uk",
                "/pl",
                "/uk/privacy",
                "/pl/privacy",
                "/privacy/2026-12-01",
                "/uk/privacy/2026-12-01",
                "/pl/privacy/2026-12-01",
            ),
            routes,
        )
    }
}
