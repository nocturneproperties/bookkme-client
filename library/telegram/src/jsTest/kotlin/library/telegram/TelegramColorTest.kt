package library.telegram

import com.varabyte.kobweb.compose.ui.graphics.Color
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import kotlin.test.Test
import kotlin.test.assertEquals

class TelegramColorTest {

    @Test
    fun `formats a color as a telegram hex color`() = runUnitTest {
        given()
        val color = Color.rgb(0x0A, 0x1B, 0xFF)

        whenn()
        val hex = color.toTelegramColor()

        then()
        assertEquals("#0a1bff", hex)
    }

    @Test
    fun `drops the alpha channel telegram does not accept`() = runUnitTest {
        given()
        val color = Color.rgba(0x12, 0x34, 0x56, 0x80)

        whenn()
        val hex = color.toTelegramColor()

        then()
        assertEquals("#123456", hex)
    }
}
