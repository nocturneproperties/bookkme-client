package me.bookk.site.designsystem.resources

enum class AppLanguage(
    val tag: String,
    val autonym: String,
) {
    EN("en", "English"),
    UK("uk", "Українська"),
    PL("pl", "Polski"),
    RU("ru", "Русский"),
    ;

    companion object {
        val DEFAULT = EN

        fun resolve(saved: String?, preferred: List<String>): AppLanguage {
            return saved?.let(::fromTag) ?: preferred.firstNotNullOfOrNull(::fromTag) ?: DEFAULT
        }

        private fun fromTag(tag: String): AppLanguage? {
            val primary = tag.substringBefore('-').lowercase()
            return entries.firstOrNull { it.tag == primary }
        }
    }
}
