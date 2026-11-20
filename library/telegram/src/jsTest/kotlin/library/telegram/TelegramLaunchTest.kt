package library.telegram

import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TelegramLaunchTest {

    @Test
    fun `detects a launch from telegram when the hash carries web app data`() = runUnitTest {
        given()
        val hash = "#tgWebAppData=query_id%3DAAH%26user%3D%257B%257D&tgWebAppVersion=8.0"

        whenn()
        val isFromTelegram = TelegramLaunch.isFromTelegram(hash)

        then()
        assertTrue(isFromTelegram)
    }

    @Test
    fun `does not detect a launch from telegram without a hash`() = runUnitTest {
        given()
        val hash = ""

        whenn()
        val isFromTelegram = TelegramLaunch.isFromTelegram(hash)

        then()
        assertFalse(isFromTelegram)
    }

    @Test
    fun `does not detect a launch from telegram from an unrelated fragment`() = runUnitTest {
        given()
        val hash = "#section=tgWebAppData"

        whenn()
        val isFromTelegram = TelegramLaunch.isFromTelegram(hash)

        then()
        assertFalse(isFromTelegram)
    }
}
