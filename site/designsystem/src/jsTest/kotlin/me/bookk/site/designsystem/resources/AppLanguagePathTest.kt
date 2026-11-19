package me.bookk.site.designsystem.resources

import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class AppLanguagePathTest {

    @Test
    fun `prefixes a path with the language tag`() = runUnitTest {
        given()
        val path = "/privacy/2026-12-01"

        whenn()
        val localized = AppLanguage.UK.localize(path)

        then()
        assertEquals("/uk/privacy/2026-12-01", localized)
    }

    @Test
    fun `localizes the home page without a trailing slash`() = runUnitTest {
        given()
        val path = "/"

        whenn()
        val localized = AppLanguage.PL.localize(path)

        then()
        assertEquals("/pl", localized)
    }

    @Test
    fun `keeps english paths without a prefix`() = runUnitTest {
        given()
        val path = "/contact"

        whenn()
        val localized = AppLanguage.EN.localize(path)

        then()
        assertEquals("/contact", localized)
    }

    @Test
    fun `replaces the prefix of an already localized path`() = runUnitTest {
        given()
        val path = "/uk/terms"

        whenn()
        val localized = AppLanguage.PL.localize(path)

        then()
        assertEquals("/pl/terms", localized)
    }

    @Test
    fun `removes the prefix when localizing to english`() = runUnitTest {
        given()
        val path = "/uk"

        whenn()
        val localized = AppLanguage.EN.localize(path)

        then()
        assertEquals("/", localized)
    }

    @Test
    fun `keeps the query and fragment of a path`() = runUnitTest {
        given()
        val path = "/privacy?ref=app#section-5"

        whenn()
        val localized = AppLanguage.UK.localize(path)

        then()
        assertEquals("/uk/privacy?ref=app#section-5", localized)
    }

    @Test
    fun `leaves fragments and external links unchanged`() = runUnitTest {
        given()
        val links =
            listOf("#section-1", "https://example.com/privacy", "mailto:support@bookkme.com")

        whenn()
        val localized = links.map { AppLanguage.PL.localize(it) }

        then()
        assertEquals(links, localized)
    }

    @Test
    fun `reads the language from the path prefix`() = runUnitTest {
        given()
        val path = "/pl/contact"

        whenn()
        val language = AppLanguage.fromPath(path)

        then()
        assertEquals(AppLanguage.PL, language)
    }

    @Test
    fun `reads no language from an unprefixed path`() = runUnitTest {
        given()
        val path = "/contact"

        whenn()
        val language = AppLanguage.fromPath(path)

        then()
        assertNull(language)
    }

    @Test
    fun `does not read a language from a segment that only starts with a tag`() = runUnitTest {
        given()
        val path = "/ukraine"

        whenn()
        val language = AppLanguage.fromPath(path)

        then()
        assertNull(language)
    }

    @Test
    fun `does not read english from an en prefix`() = runUnitTest {
        given()
        val path = "/en/contact"

        whenn()
        val language = AppLanguage.fromPath(path)

        then()
        assertNull(language)
    }
}
