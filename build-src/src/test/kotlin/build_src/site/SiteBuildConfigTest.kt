package build_src.site

import kotlin.test.Test
import kotlin.test.assertEquals

class SiteBuildConfigTest {
    @Test
    fun `writes the origin, page paths and languages as a kotlin object`() {
        val source = SiteBuildConfig.kotlin(
            origin = "https://bookkme.com",
            pagePaths = listOf("/", "/contact"),
            languages = listOf("en", "uk", "pl"),
        )

        assertEquals(
            """
            package me.bookk.site.config

            object SiteBuildConfig {
                const val ORIGIN = "https://bookkme.com"
                val pagePaths: List<String> = listOf("/", "/contact")
                val languages: List<String> = listOf("en", "uk", "pl")
            }

            """.trimIndent(),
            source,
        )
    }

    @Test
    fun `escapes kotlin string templates and quotes`() {
        val source = SiteBuildConfig.kotlin(
            origin = "https://example.com/\$x\"",
            pagePaths = emptyList(),
            languages = emptyList(),
        )

        assertEquals(
            "    const val ORIGIN = \"https://example.com/\\\$x\\\"\"",
            source.lines().single { "ORIGIN" in it },
        )
    }
}
