package me.bookk.designsystem.theme

import com.varabyte.kobweb.compose.ui.graphics.Color
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.designsystem.resources.color.ColorToken
import kotlin.test.Test
import kotlin.test.assertEquals

class ColorResolverTest {

    @Test
    fun `resolves tokens to the light design system palette`() = runUnitTest {
        given()
        val colors = WebColors.light

        whenn()
        val background = ColorToken.Background.resolve(colors)
        val action = ColorToken.ActionText.resolve(colors)

        then()
        assertEquals(Color.rgb(0xF2F2F7).toRgb(), background.toRgb())
        assertEquals(Color.rgb(0x0A84FF).toRgb(), action.toRgb())
    }

    @Test
    fun `resolves tokens to the dark design system palette`() = runUnitTest {
        given()
        val colors = WebColors.dark

        whenn()
        val elevated = ColorToken.Elevated.resolve(colors)
        val error = ColorToken.Error.resolve(colors)

        then()
        assertEquals(Color.rgb(0x1C1C1E).toRgb(), elevated.toRgb())
        assertEquals(Color.rgb(0xFF6961).toRgb(), error.toRgb())
    }
}
