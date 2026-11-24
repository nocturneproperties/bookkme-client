package me.bookk.designsystem.head

import me.bookk.designsystem.resources.AppLanguage

enum class MetaAttribute(
    val html: String,
) {
    NAME("name"),
    PROPERTY("property"),
}

data class HeadMeta(
    val attribute: MetaAttribute,
    val key: String,
    val content: String,
) {
    companion object {
        fun name(key: String, content: String): HeadMeta {
            return HeadMeta(MetaAttribute.NAME, key, content)
        }

        fun property(key: String, content: String): HeadMeta {
            return HeadMeta(MetaAttribute.PROPERTY, key, content)
        }
    }
}

class PageMeta(
    val path: String,
    val title: String,
    val description: String,
    val isIndexed: Boolean,
)

object PageMetaTags {
    fun forPage(origin: String, page: PageMeta): List<HeadMeta> {
        val description = HeadMeta.name("description", page.description)
        if (!page.isIndexed) return listOf(description, HeadMeta.name("robots", "noindex"))
        return listOf(description) + openGraph(origin, page)
    }

    private fun openGraph(origin: String, page: PageMeta): List<HeadMeta> {
        val language = AppLanguage.fromPath(page.path) ?: AppLanguage.DEFAULT
        val alternates = AppLanguage.entries
            .filter { it != language }
            .map { HeadMeta.property("og:locale:alternate", it.locale) }
        return listOf(
            HeadMeta.property("og:title", page.title),
            HeadMeta.property("og:description", page.description),
            HeadMeta.property("og:url", origin + language.localize(page.path)),
            HeadMeta.property("og:locale", language.locale),
        ) + alternates
    }
}
