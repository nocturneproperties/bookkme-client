package build_src.site

import groovy.json.JsonGenerator
import groovy.json.JsonOutput

object WebManifest {
    const val FILE_NAME = "site.webmanifest"

    private val generator = JsonGenerator.Options()
        .disableUnicodeEscaping()
        .build()

    fun json(
        name: String,
        description: String,
        themeColor: String,
        backgroundColor: String,
    ): String {
        val manifest = linkedMapOf(
            "name" to name,
            "short_name" to name,
            "description" to description,
            "start_url" to "/",
            "theme_color" to themeColor,
            "background_color" to backgroundColor,
            "icons" to listOf(
                icon(size = 192, purpose = "any"),
                icon(size = 512, purpose = "any"),
                icon(size = 512, purpose = "maskable"),
            ),
        )
        return JsonOutput.prettyPrint(generator.toJson(manifest), true) + "\n"
    }

    private fun icon(size: Int, purpose: String): Map<String, String> {
        return linkedMapOf(
            "src" to "/icon-$size.png",
            "sizes" to "${size}x$size",
            "type" to "image/png",
            "purpose" to purpose,
        )
    }
}
