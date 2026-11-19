package me.bookk.site.designsystem.theme

import com.varabyte.kobweb.silk.theme.colors.ColorMode
import me.bookk.core.test.given
import me.bookk.core.test.runUnitTest
import me.bookk.core.test.then
import me.bookk.core.test.whenn
import me.bookk.designsystem.resources.color.ColorToken
import kotlin.test.Test
import kotlin.test.assertEquals

class BandSurfacesTest {

    @Test
    fun `puts elevated cards on the page background in a plain band`() = runUnitTest {
        given()
        val tone = BandTone.PLAIN

        whenn()
        val surfaces = tone.surfaces()

        then()
        assertEquals(
            BandSurfaces(ColorToken.Background, ColorToken.Elevated, ColorToken.Background),
            surfaces,
        )
    }

    @Test
    fun `puts page background cards on the elevated colour in an alternate band`() = runUnitTest {
        given()
        val tone = BandTone.ALTERNATE

        whenn()
        val surfaces = tone.surfaces()

        then()
        assertEquals(
            BandSurfaces(ColorToken.Elevated, ColorToken.Background, ColorToken.Elevated),
            surfaces,
        )
    }

    @Test
    fun `paints a dark band with the plain surfaces`() = runUnitTest {
        given()
        val tone = BandTone.DARK

        whenn()
        val surfaces = tone.surfaces()

        then()
        assertEquals(BandTone.PLAIN.surfaces(), surfaces)
    }

    @Test
    fun `renders a dark band in dark mode when the page is light`() = runUnitTest {
        given()
        val tone = BandTone.DARK

        whenn()
        val mode = tone.colorMode(ColorMode.LIGHT)

        then()
        assertEquals(ColorMode.DARK, mode)
    }

    @Test
    fun `renders an alternate band in the page colour mode`() = runUnitTest {
        given()
        val tone = BandTone.ALTERNATE

        whenn()
        val mode = tone.colorMode(ColorMode.LIGHT)

        then()
        assertEquals(ColorMode.LIGHT, mode)
    }
}
