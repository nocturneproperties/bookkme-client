package library.telegram

import com.varabyte.kobweb.silk.theme.colors.ColorMode
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.designsystem.resources.AppLanguage
import kotlin.test.Test
import kotlin.test.assertEquals

class TelegramSessionTest {

    @Test
    fun `uses dark mode when telegram uses the dark scheme`() = runUnitTest {
        given()
        val colorScheme = "dark"

        whenn()
        val session = TelegramSession.from(colorScheme, "en", emptyList())

        then()
        assertEquals(ColorMode.DARK, session.colorMode)
    }

    @Test
    fun `uses light mode when telegram uses the light scheme`() = runUnitTest {
        given()
        val colorScheme = "light"

        whenn()
        val session = TelegramSession.from(colorScheme, "en", emptyList())

        then()
        assertEquals(ColorMode.LIGHT, session.colorMode)
    }

    @Test
    fun `uses the language of the telegram user`() = runUnitTest {
        given()
        val languageCode = "pl"

        whenn()
        val session = TelegramSession.from("light", languageCode, listOf("uk-UA"))

        then()
        assertEquals(AppLanguage.PL, session.language)
    }

    @Test
    fun `falls back to the browser language when the telegram language is not supported`() =
        runUnitTest {
            given()
            val languageCode = "ru"

            whenn()
            val session = TelegramSession.from("light", languageCode, listOf("uk-UA"))

            then()
            assertEquals(AppLanguage.UK, session.language)
        }

    @Test
    fun `falls back to the browser language when telegram sends no language`() = runUnitTest {
        given()
        val languageCode = null

        whenn()
        val session = TelegramSession.from("light", languageCode, listOf("pl-PL"))

        then()
        assertEquals(AppLanguage.PL, session.language)
    }
}
