package library.device.impl

import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import kotlin.test.Test
import kotlin.test.assertEquals

class BrowserDeviceNameTest {

    @Test
    fun `names chrome on windows`() = runUnitTest {
        given()
        val userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 " +
            "(KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36"

        whenn()
        val name = BrowserDeviceName.from(userAgent)

        then()
        assertEquals("Chrome on Windows", name)
    }

    @Test
    fun `names safari on macos`() = runUnitTest {
        given()
        val userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 " +
            "(KHTML, like Gecko) Version/18.1 Safari/605.1.15"

        whenn()
        val name = BrowserDeviceName.from(userAgent)

        then()
        assertEquals("Safari on macOS", name)
    }

    @Test
    fun `names an iphone as ios rather than macos`() = runUnitTest {
        given()
        val userAgent =
            "Mozilla/5.0 (iPhone; CPU iPhone OS 18_1 like Mac OS X) AppleWebKit/605.1.15 " +
                "(KHTML, like Gecko) Version/18.1 Mobile/15E148 Safari/604.1"

        whenn()
        val name = BrowserDeviceName.from(userAgent)

        then()
        assertEquals("Safari on iOS", name)
    }

    @Test
    fun `names chrome on ios by its ios token`() = runUnitTest {
        given()
        val userAgent =
            "Mozilla/5.0 (iPhone; CPU iPhone OS 18_1 like Mac OS X) AppleWebKit/605.1.15 " +
                "(KHTML, like Gecko) CriOS/131.0.6778.73 Mobile/15E148 Safari/604.1"

        whenn()
        val name = BrowserDeviceName.from(userAgent)

        then()
        assertEquals("Chrome on iOS", name)
    }

    @Test
    fun `names edge rather than the chrome it reports`() = runUnitTest {
        given()
        val userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 " +
            "(KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36 Edg/131.0.0.0"

        whenn()
        val name = BrowserDeviceName.from(userAgent)

        then()
        assertEquals("Edge on Windows", name)
    }

    @Test
    fun `names android rather than the linux it reports`() = runUnitTest {
        given()
        val userAgent = "Mozilla/5.0 (Android 14; Mobile; rv:132.0) Gecko/132.0 Firefox/132.0"

        whenn()
        val name = BrowserDeviceName.from(userAgent)

        then()
        assertEquals("Firefox on Android", name)
    }

    @Test
    fun `names only the system for an in-app web view`() = runUnitTest {
        given()
        val userAgent =
            "Mozilla/5.0 (iPhone; CPU iPhone OS 18_1 like Mac OS X) AppleWebKit/605.1.15 " +
                "(KHTML, like Gecko) Mobile/15E148"

        whenn()
        val name = BrowserDeviceName.from(userAgent)

        then()
        assertEquals("iOS", name)
    }

    @Test
    fun `falls back to a generic name for an unknown user agent`() = runUnitTest {
        given()
        val userAgent = "curl/8.7.1"

        whenn()
        val name = BrowserDeviceName.from(userAgent)

        then()
        assertEquals("Web browser", name)
    }
}
