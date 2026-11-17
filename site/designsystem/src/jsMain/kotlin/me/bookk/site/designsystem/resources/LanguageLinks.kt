package me.bookk.site.designsystem.resources

data class HeadLink(
    val rel: String,
    val href: String,
    val hreflang: String? = null,
)

object LanguageLinks {
    private const val CANONICAL = "canonical"
    private const val ALTERNATE = "alternate"
    private const val DEFAULT_HREFLANG = "x-default"

    fun forPath(origin: String, path: String): List<HeadLink> {
        val language = AppLanguage.fromPath(path) ?: AppLanguage.DEFAULT
        val canonical = HeadLink(CANONICAL, origin + language.localize(path))
        val alternates = AppLanguage.entries.map {
            HeadLink(ALTERNATE, origin + it.localize(path), it.tag)
        }
        val fallback =
            HeadLink(ALTERNATE, origin + AppLanguage.DEFAULT.localize(path), DEFAULT_HREFLANG)
        return listOf(canonical) + alternates + fallback
    }
}
