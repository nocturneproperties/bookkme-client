package build_src.site

import groovy.xml.XmlSlurper
import groovy.xml.slurpersupport.GPathResult
import groovy.xml.slurpersupport.NodeChild
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SitemapTest {
    private val pages = SitePages(
        pagePaths = listOf("/", "/contact"),
        versionedPaths = listOf("/privacy/2026-12-01"),
        defaultLanguage = "en",
        otherLanguages = listOf("uk", "pl"),
    )

    private fun urls(xml: String): List<NodeChild> {
        val root = XmlSlurper(false, true).parseText(xml)
        return (root.getProperty("url") as GPathResult).map { it as NodeChild }
    }

    private fun NodeChild.loc(): String {
        return (getProperty("loc") as GPathResult).text()
    }

    private fun NodeChild.alternates(): Map<String, String> {
        val links = getProperty("link") as GPathResult
        return links.associate {
            val attributes = (it as NodeChild).attributes()
            attributes["hreflang"].toString() to attributes["href"].toString()
        }
    }

    @Test
    fun `lists every page in every language`() {
        val xml = Sitemap.xml(ORIGIN, pages)

        val locations = urls(xml).map { it.loc() }

        assertEquals(
            listOf(
                "$ORIGIN/",
                "$ORIGIN/uk",
                "$ORIGIN/pl",
                "$ORIGIN/contact",
                "$ORIGIN/uk/contact",
                "$ORIGIN/pl/contact",
                "$ORIGIN/privacy/2026-12-01",
                "$ORIGIN/uk/privacy/2026-12-01",
                "$ORIGIN/pl/privacy/2026-12-01",
            ),
            locations,
        )
    }

    @Test
    fun `links every language of a page and the default language as x-default`() {
        val xml = Sitemap.xml(ORIGIN, pages)

        val alternates = urls(xml).first { it.loc() == "$ORIGIN/uk/contact" }.alternates()

        assertEquals(
            mapOf(
                "en" to "$ORIGIN/contact",
                "uk" to "$ORIGIN/uk/contact",
                "pl" to "$ORIGIN/pl/contact",
                "x-default" to "$ORIGIN/contact",
            ),
            alternates,
        )
    }

    @Test
    fun `declares the sitemap and xhtml namespaces`() {
        val xml = Sitemap.xml(ORIGIN, pages)

        assertTrue("xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\"" in xml)
        assertTrue("xmlns:xhtml=\"http://www.w3.org/1999/xhtml\"" in xml)
    }

    @Test
    fun `escapes xml special characters in urls`() {
        val special = SitePages(listOf("/a&b"), emptyList(), "en", emptyList())

        val xml = Sitemap.xml(ORIGIN, special)

        assertEquals("$ORIGIN/a&b", urls(xml).single().loc())
    }

    private companion object {
        const val ORIGIN = "https://bookkme.com"
    }
}
