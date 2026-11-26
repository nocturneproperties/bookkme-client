package library.telegram

import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TelegramWriteAccessTest {

    private class Fixture(
        version: String,
        grants: Boolean = true,
    ) {
        var requests = 0
        val webApp: TelegramWebApp = js("{}").unsafeCast<TelegramWebApp>().also { app ->
            val dynamicApp = app.asDynamic()
            dynamicApp.isVersionAtLeast = { minVersion: String ->
                minVersion.toVersion() <= version.toVersion()
            }
            dynamicApp.requestWriteAccess = { onClosed: (Boolean) -> Unit ->
                requests++
                onClosed(grants)
            }
        }

        private fun String.toVersion(): List<Int> {
            return split(".").map { it.toInt() }.let { it + List(2 - it.size) { 0 } }
        }

        private operator fun List<Int>.compareTo(other: List<Int>): Int {
            return zip(other).map { (a, b) -> a.compareTo(b) }.firstOrNull { it != 0 } ?: 0
        }
    }

    @Test
    fun `reports write access the person granted`() = runUnitTest {
        given()
        val fixture = Fixture(version = "6.9", grants = true)

        whenn()
        val isGranted = fixture.webApp.askWriteAccess()

        then()
        assertTrue(isGranted)
        assertEquals(1, fixture.requests)
    }

    @Test
    fun `reports write access the person refused`() = runUnitTest {
        given()
        val fixture = Fixture(version = "8.0", grants = false)

        whenn()
        val isGranted = fixture.webApp.askWriteAccess()

        then()
        assertFalse(isGranted)
    }

    @Test
    fun `does not ask a telegram client without write access requests`() = runUnitTest {
        given()
        val fixture = Fixture(version = "6.8")

        whenn()
        val isGranted = fixture.webApp.askWriteAccess()

        then()
        assertFalse(isGranted)
        assertEquals(0, fixture.requests)
    }
}
