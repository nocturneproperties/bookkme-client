package library.telegram

import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import kotlin.test.Test
import kotlin.test.assertEquals

class TelegramStartParamTest {

    @Test
    fun `returns the start param on a launch that has not been handled`() = runUnitTest {
        given()
        val handledLaunchId = null

        whenn()
        val startParam = TelegramStartParam.unhandled("my-salon", "launch-1", handledLaunchId)

        then()
        assertEquals("my-salon", startParam)
    }

    @Test
    fun `returns no start param when the same launch is reloaded`() = runUnitTest {
        given()
        val handledLaunchId = "launch-1"

        whenn()
        val startParam = TelegramStartParam.unhandled("my-salon", "launch-1", handledLaunchId)

        then()
        assertEquals("", startParam)
    }

    @Test
    fun `returns the start param when the same link opens a new launch`() = runUnitTest {
        given()
        val handledLaunchId = "launch-1"

        whenn()
        val startParam = TelegramStartParam.unhandled("my-salon", "launch-2", handledLaunchId)

        then()
        assertEquals("my-salon", startParam)
    }

    @Test
    fun `returns the start param when the launch has no id`() = runUnitTest {
        given()
        val handledLaunchId = null

        whenn()
        val startParam = TelegramStartParam.unhandled("my-salon", null, handledLaunchId)

        then()
        assertEquals("my-salon", startParam)
    }

    @Test
    fun `returns no start param when the launch has none`() = runUnitTest {
        given()
        val handledLaunchId = null

        whenn()
        val startParam = TelegramStartParam.unhandled(null, "launch-1", handledLaunchId)

        then()
        assertEquals("", startParam)
    }
}
