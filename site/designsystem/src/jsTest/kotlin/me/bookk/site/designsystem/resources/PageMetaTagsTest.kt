package me.bookk.site.designsystem.resources

import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import kotlin.test.Test
import kotlin.test.assertEquals

class PageMetaTagsTest {

    private fun page(path: String, isIndexed: Boolean = true): PageMeta {
        return PageMeta(
            path = path,
            title = "Contact us",
            description = "How to reach us",
            isIndexed = isIndexed,
        )
    }

    @Test
    fun `describes the page for search engines and link previews`() = runUnitTest {
        given()
        val page = page("/contact")

        whenn()
        val tags = PageMetaTags.forPage(ORIGIN, page)

        then()
        assertEquals(
            listOf(
                HeadMeta.name("description", "How to reach us"),
                HeadMeta.property("og:title", "Contact us"),
                HeadMeta.property("og:description", "How to reach us"),
                HeadMeta.property("og:url", "$ORIGIN/contact"),
            ),
            tags.take(4),
        )
    }

    @Test
    fun `points the preview url at the localized page`() = runUnitTest {
        given()
        val page = page("/uk/")

        whenn()
        val tags = PageMetaTags.forPage(ORIGIN, page)

        then()
        assertEquals(HeadMeta.property("og:url", "$ORIGIN/uk"), tags.single { it.key == "og:url" })
    }

    @Test
    fun `names the page locale and the other languages as alternates`() = runUnitTest {
        given()
        val page = page("/pl/terms")

        whenn()
        val tags = PageMetaTags.forPage(ORIGIN, page)

        then()
        assertEquals(
            listOf(
                HeadMeta.property("og:locale", "pl_PL"),
                HeadMeta.property("og:locale:alternate", "en_US"),
                HeadMeta.property("og:locale:alternate", "uk_UA"),
            ),
            tags.filter { it.key.startsWith("og:locale") },
        )
    }

    @Test
    fun `uses the default language locale on an unprefixed page`() = runUnitTest {
        given()
        val page = page("/privacy/2026-12-01")

        whenn()
        val tags = PageMetaTags.forPage(ORIGIN, page)

        then()
        assertEquals(HeadMeta.property("og:locale", "en_US"), tags.single { it.key == "og:locale" })
    }

    @Test
    fun `keeps an unindexed page out of search results and previews`() = runUnitTest {
        given()
        val page = page("/missing", isIndexed = false)

        whenn()
        val tags = PageMetaTags.forPage(ORIGIN, page)

        then()
        assertEquals(
            listOf(
                HeadMeta.name("description", "How to reach us"),
                HeadMeta.name("robots", "noindex"),
            ),
            tags,
        )
    }

    private companion object {
        const val ORIGIN = "https://bookkme.com"
    }
}
