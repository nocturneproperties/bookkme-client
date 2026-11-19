package build_src.site

import org.gradle.api.GradleException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class DesignSystemColorsTest {
    private val xml = """
        <?xml version="1.0" encoding="utf-8"?>
        <resources>
            <color name="lightBackground">#F2F2F7</color>
            <!--Dark-->
            <color name="darkBackground">#000000</color>
            <color name="lightBlurredBackground">#FFFFFFBF</color>
            <color name="lightDivider">#dedee0</color>
        </resources>
    """.trimIndent()

    @Test
    fun `returns an opaque color by name`() {
        val colors = DesignSystemColors.parse(xml)

        val color = colors.opaque("lightBackground")

        assertEquals("#F2F2F7", color)
    }

    @Test
    fun `returns opaque colors in upper case`() {
        val colors = DesignSystemColors.parse(xml)

        val color = colors.opaque("lightDivider")

        assertEquals("#DEDEE0", color)
    }

    @Test
    fun `rejects a color with an alpha channel`() {
        val colors = DesignSystemColors.parse(xml)

        val error = assertFailsWith<GradleException> { colors.opaque("lightBlurredBackground") }

        assertEquals(
            "Design system color 'lightBlurredBackground' is #FFFFFFBF, expected an opaque #RRGGBB color",
            error.message,
        )
    }

    @Test
    fun `rejects an unknown color name`() {
        val colors = DesignSystemColors.parse(xml)

        val error = assertFailsWith<GradleException> { colors.opaque("missing") }

        assertTrue("'missing' is not defined" in error.message.orEmpty())
    }
}
