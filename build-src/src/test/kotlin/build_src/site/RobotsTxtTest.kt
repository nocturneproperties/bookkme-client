package build_src.site

import kotlin.test.Test
import kotlin.test.assertEquals

class RobotsTxtTest {
    @Test
    fun `allows every crawler and points at the sitemap`() {
        val text = RobotsTxt.text("https://bookkme.com")

        assertEquals(
            "User-agent: *\nAllow: /\n\nSitemap: https://bookkme.com/sitemap.xml\n",
            text,
        )
    }
}
