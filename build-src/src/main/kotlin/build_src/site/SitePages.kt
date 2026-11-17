package build_src.site

class LocalizedPage(
    val urls: Map<String, String>,
)

class SitePages(
    private val pagePaths: List<String>,
    private val versionedPaths: List<String>,
    val defaultLanguage: String,
    private val otherLanguages: List<String>,
) {
    private val languages: List<String>
        get() = listOf(defaultLanguage) + otherLanguages

    fun localized(): List<LocalizedPage> {
        return (pagePaths + versionedPaths).map { path ->
            LocalizedPage(languages.associateWith { localize(it, path) })
        }
    }

    fun extraExportRoutes(): List<String> {
        val fileRouted = pagePaths.toSet()
        return localized()
            .flatMap { it.urls.values }
            .filterNot { it in fileRouted }
    }

    private fun localize(language: String, path: String): String {
        if (language == defaultLanguage) return path
        if (path == "/") return "/$language"
        return "/$language$path"
    }
}
