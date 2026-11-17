package build_src.site

import org.gradle.api.GradleException
import java.io.File
import java.nio.file.Files
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class MokoStringsTest {
    private fun strings(vararg entries: Pair<String, String>): MokoStrings {
        val file = File(Files.createTempDirectory("moko").toFile(), "strings.xml")
        file.writeText(
            buildString {
                appendLine("""<?xml version="1.0" encoding="UTF-8"?>""")
                appendLine("<resources>")
                entries.forEach { (key, value) ->
                    appendLine("""    <string name="$key">$value</string>""")
                }
                appendLine("</resources>")
            },
        )
        return MokoStrings.read(file)
    }

    @Test
    fun `reads a string by its key`() {
        val strings = strings("site_app_name" to "BookkMe", "site_description" to "A booking app")

        val value = strings.value("site_description")

        assertEquals("A booking app", value)
    }

    @Test
    fun `unescapes quotes and xml entities`() {
        val strings = strings("site_description" to """Today\'s &amp; \"tomorrow\'s\" bookings""")

        val value = strings.value("site_description")

        assertEquals("""Today's & "tomorrow's" bookings""", value)
    }

    @Test
    fun `fails on a missing key`() {
        val strings = strings("site_app_name" to "BookkMe")

        assertFailsWith<GradleException> { strings.value("site_description") }
    }
}
