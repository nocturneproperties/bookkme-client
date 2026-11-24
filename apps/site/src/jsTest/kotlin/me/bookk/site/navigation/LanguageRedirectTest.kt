package me.bookk.site.navigation

import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.designsystem.resources.AppLanguage
import kotlin.test.Test
import kotlin.test.assertEquals

class LanguageRedirectTest {

    @Test
    fun `sends an unprefixed path to the preferred language`() = runUnitTest {
        given()
        val path = "/contact"

        whenn()
        val target = LanguageRedirect.target(path, AppLanguage.UK)

        then()
        assertEquals("/uk/contact", target)
    }

    @Test
    fun `keeps an unprefixed path when english is preferred`() = runUnitTest {
        given()
        val path = "/contact"

        whenn()
        val target = LanguageRedirect.target(path, AppLanguage.EN)

        then()
        assertEquals("/contact", target)
    }

    @Test
    fun `keeps the language of a localized path over the preferred one`() = runUnitTest {
        given()
        val path = "/pl/terms"

        whenn()
        val target = LanguageRedirect.target(path, AppLanguage.UK)

        then()
        assertEquals("/pl/terms", target)
    }

    @Test
    fun `moves a legal document language path to the localized version`() = runUnitTest {
        given()
        val path = "/dpa/2026-12-01/pl"

        whenn()
        val target = LanguageRedirect.target(path, AppLanguage.UK)

        then()
        assertEquals("/pl/dpa/2026-12-01", target)
    }

    @Test
    fun `keeps english from a legal document language path over the preferred language`() =
        runUnitTest {
            given()
            val path = "/privacy/2026-12-01/en"

            whenn()
            val target = LanguageRedirect.target(path, AppLanguage.PL)

            then()
            assertEquals("/privacy/2026-12-01", target)
        }

    @Test
    fun `leaves a legal document path in an unsupported language unchanged`() = runUnitTest {
        given()
        val path = "/terms/2026-12-01/ru"

        whenn()
        val target = LanguageRedirect.target(path, AppLanguage.EN)

        then()
        assertEquals("/terms/2026-12-01/ru", target)
    }
}
