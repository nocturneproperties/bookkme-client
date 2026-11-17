package build_src.site

object Sitemap {
    const val FILE_NAME = "sitemap.xml"
    private const val DEFAULT_HREFLANG = "x-default"

    fun xml(origin: String, pages: SitePages): String {
        return buildString {
            appendLine("""<?xml version="1.0" encoding="UTF-8"?>""")
            appendLine(
                """<urlset xmlns="http://www.sitemaps.org/schemas/sitemap/0.9" """ +
                    """xmlns:xhtml="http://www.w3.org/1999/xhtml">""",
            )
            pages.localized().forEach { page ->
                page.urls.values.forEach { append(url(origin, it, page, pages.defaultLanguage)) }
            }
            appendLine("</urlset>")
        }
    }

    private fun url(
        origin: String,
        path: String,
        page: LocalizedPage,
        defaultLanguage: String,
    ): String {
        val alternates = page.urls + (DEFAULT_HREFLANG to page.urls.getValue(defaultLanguage))
        return buildString {
            appendLine("  <url>")
            appendLine("    <loc>${(origin + path).escaped()}</loc>")
            alternates.forEach { (hreflang, alternate) ->
                appendLine(
                    """    <xhtml:link rel="alternate" hreflang="$hreflang" """ +
                        """href="${(origin + alternate).escaped()}"/>""",
                )
            }
            appendLine("  </url>")
        }
    }

    private fun String.escaped(): String {
        return replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&apos;")
    }
}

object RobotsTxt {
    const val FILE_NAME = "robots.txt"

    fun text(origin: String): String {
        return "User-agent: *\nAllow: /\n\nSitemap: $origin/${Sitemap.FILE_NAME}\n"
    }
}
