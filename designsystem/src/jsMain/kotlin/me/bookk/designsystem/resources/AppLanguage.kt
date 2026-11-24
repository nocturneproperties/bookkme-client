package me.bookk.designsystem.resources

enum class AppLanguage(
    val tag: String,
    val autonym: String,
    val abbreviation: String,
    val locale: String,
) {
    EN("en", "English", "EN", "en_US"),
    UK("uk", "Українська", "UA", "uk_UA"),
    PL("pl", "Polski", "PL", "pl_PL"),
    ;

    private val pathPrefix: String
        get() = if (this == DEFAULT) "" else "/$tag"

    fun localize(path: String): String {
        if (!path.startsWith("/")) return path
        val routeEnd = path.indexOfAny(charArrayOf('?', '#')).takeIf { it >= 0 } ?: path.length
        return prefixed(unprefixed(path.substring(0, routeEnd))) + path.substring(routeEnd)
    }

    private fun prefixed(route: String): String {
        if (route == "/" && pathPrefix.isNotEmpty()) return pathPrefix
        return pathPrefix + route
    }

    companion object {
        val DEFAULT = EN

        fun resolve(saved: String?, preferred: List<String>): AppLanguage {
            return saved?.let(::fromTag) ?: preferred.firstNotNullOfOrNull(::fromTag) ?: DEFAULT
        }

        fun fromPath(path: String): AppLanguage? {
            val segment = path
                .removePrefix("/")
                .substringBefore('/')
                .substringBefore('?')
                .substringBefore('#')
            return entries.firstOrNull { it != DEFAULT && it.tag == segment }
        }

        private fun unprefixed(route: String): String {
            val language = fromPath(route) ?: return route
            return route.removePrefix(language.pathPrefix).ifEmpty { "/" }
        }

        private fun fromTag(tag: String): AppLanguage? {
            val primary = tag.substringBefore('-').lowercase()
            return entries.firstOrNull { it.tag == primary }
        }
    }
}
