package me.bookk.site.designsystem.head

import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import kotlin.test.Test
import kotlin.test.assertEquals

class LanguageLinksTest {

    @Test
    fun `points the canonical link at the localized page`() = runUnitTest {
        given()
        val path = "/uk/contact"

        whenn()
        val links = LanguageLinks.forPath(ORIGIN, path)

        then()
        assertEquals(HeadLink("canonical", "$ORIGIN/uk/contact"), links.first())
    }

    @Test
    fun `links every language and english as the default`() = runUnitTest {
        given()
        val path = "/pl/privacy/2026-12-01"

        whenn()
        val links = LanguageLinks.forPath(ORIGIN, path)

        then()
        assertEquals(
            listOf(
                HeadLink("alternate", "$ORIGIN/privacy/2026-12-01", "en"),
                HeadLink("alternate", "$ORIGIN/uk/privacy/2026-12-01", "uk"),
                HeadLink("alternate", "$ORIGIN/pl/privacy/2026-12-01", "pl"),
                HeadLink("alternate", "$ORIGIN/privacy/2026-12-01", "x-default"),
            ),
            links.drop(1),
        )
    }

    @Test
    fun `links the home page of every language`() = runUnitTest {
        given()
        val path = "/"

        whenn()
        val links = LanguageLinks.forPath(ORIGIN, path)

        then()
        assertEquals(
            listOf("$ORIGIN/", "$ORIGIN/", "$ORIGIN/uk", "$ORIGIN/pl", "$ORIGIN/"),
            links.map { it.href },
        )
    }

    @Test
    fun `normalizes a trailing slash after the language prefix`() = runUnitTest {
        given()
        val path = "/uk/"

        whenn()
        val links = LanguageLinks.forPath(ORIGIN, path)

        then()
        assertEquals(HeadLink("canonical", "$ORIGIN/uk"), links.first())
    }

    private companion object {
        const val ORIGIN = "https://bookkme.com"
    }
}
