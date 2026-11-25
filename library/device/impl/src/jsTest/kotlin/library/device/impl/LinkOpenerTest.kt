package library.device.impl

import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import kotlin.test.Test
import kotlin.test.assertEquals

class LinkOpenerTest {

    private class FakeTelegramWebApp(
        override val initData: String,
    ) : TelegramLinkHost {
        val openedLinks = mutableListOf<String>()

        override fun openLink(url: String) {
            openedLinks.add(url)
        }
    }

    private class Fixture(
        webApp: TelegramLinkHost?,
    ) {
        val browserLinks = mutableListOf<String>()
        val sut = LinkOpener(
            telegramWebApp = { webApp },
            openInBrowser = { browserLinks.add(it) },
        )
    }

    @Test
    fun `opens links through telegram inside a mini app`() = runUnitTest {
        given()
        val webApp = FakeTelegramWebApp(initData = "query_id=1&user=2&hash=3")
        val fixture = Fixture(webApp)

        whenn()
        fixture.sut.open("https://bookkme.com/privacy")

        then()
        assertEquals(listOf("https://bookkme.com/privacy"), webApp.openedLinks)
        assertEquals(emptyList(), fixture.browserLinks)
    }

    @Test
    fun `opens links in the browser outside telegram`() = runUnitTest {
        given()
        val fixture = Fixture(webApp = null)

        whenn()
        fixture.sut.open("https://bookkme.com/privacy")

        then()
        assertEquals(listOf("https://bookkme.com/privacy"), fixture.browserLinks)
    }

    @Test
    fun `opens links in the browser when the telegram script runs without launch data`() =
        runUnitTest {
            given()
            val webApp = FakeTelegramWebApp(initData = "")
            val fixture = Fixture(webApp)

            whenn()
            fixture.sut.open("https://bookkme.com/privacy")

            then()
            assertEquals(listOf("https://bookkme.com/privacy"), fixture.browserLinks)
            assertEquals(emptyList(), webApp.openedLinks)
        }
}
