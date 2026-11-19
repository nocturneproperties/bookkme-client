package build_src.site

import groovy.json.JsonSlurper
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class WebManifestTest {
    private fun manifest(description: String = "A booking app"): Map<*, *> {
        val json = WebManifest.json(
            name = "BookkMe",
            description = description,
            themeColor = "#F2F2F7",
            backgroundColor = "#000000",
        )
        return JsonSlurper().parseText(json) as Map<*, *>
    }

    @Test
    fun `names the site`() {
        val manifest = manifest()

        assertEquals("BookkMe", manifest["name"])
        assertEquals("BookkMe", manifest["short_name"])
    }

    @Test
    fun `uses the given theme and background colors`() {
        val manifest = manifest()

        assertEquals("#F2F2F7", manifest["theme_color"])
        assertEquals("#000000", manifest["background_color"])
    }

    @Test
    fun `starts at the site root`() {
        val manifest = manifest()

        assertEquals("/", manifest["start_url"])
    }

    @Test
    fun `lists the 192 and 512 pixel icons and a maskable icon`() {
        val manifest = manifest()

        val icons = manifest["icons"] as List<*>

        assertEquals(
            listOf(
                mapOf(
                    "src" to "/icon-192.png",
                    "sizes" to "192x192",
                    "type" to "image/png",
                    "purpose" to "any",
                ),
                mapOf(
                    "src" to "/icon-512.png",
                    "sizes" to "512x512",
                    "type" to "image/png",
                    "purpose" to "any",
                ),
                mapOf(
                    "src" to "/icon-512.png",
                    "sizes" to "512x512",
                    "type" to "image/png",
                    "purpose" to "maskable",
                ),
            ),
            icons,
        )
    }

    @Test
    fun `writes non-ASCII characters without escaping them`() {
        val json = WebManifest.json(
            name = "BookkMe",
            description = "BookkMe — a booking app",
            themeColor = "#F2F2F7",
            backgroundColor = "#000000",
        )

        assertTrue("\"BookkMe — a booking app\"" in json)
    }

    @Test
    fun `keeps a description with quotes and non-ASCII characters intact`() {
        val description = "BookkMe — a \"booking\" app"

        val manifest = manifest(description)

        assertEquals(description, manifest["description"])
    }
}
