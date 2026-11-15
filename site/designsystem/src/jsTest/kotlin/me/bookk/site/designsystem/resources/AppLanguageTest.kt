package me.bookk.site.designsystem.resources

import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import kotlin.test.Test
import kotlin.test.assertEquals

class AppLanguageTest {

    @Test
    fun `prefers the saved language`() = runUnitTest {
        given()
        val preferred = listOf("pl-PL")

        whenn()
        val language = AppLanguage.resolve(saved = "uk", preferred = preferred)

        then()
        assertEquals(AppLanguage.UK, language)
    }

    @Test
    fun `ignores a saved language that is not supported`() = runUnitTest {
        given()
        val preferred = listOf("ru")

        whenn()
        val language = AppLanguage.resolve(saved = "de", preferred = preferred)

        then()
        assertEquals(AppLanguage.RU, language)
    }

    @Test
    fun `matches the first supported browser language by its primary subtag`() = runUnitTest {
        given()
        val preferred = listOf("de-DE", "pl-PL", "en-US")

        whenn()
        val language = AppLanguage.resolve(saved = null, preferred = preferred)

        then()
        assertEquals(AppLanguage.PL, language)
    }

    @Test
    fun `falls back to english when no browser language is supported`() = runUnitTest {
        given()
        val preferred = listOf("de-DE", "fr")

        whenn()
        val language = AppLanguage.resolve(saved = null, preferred = preferred)

        then()
        assertEquals(AppLanguage.EN, language)
    }
}
