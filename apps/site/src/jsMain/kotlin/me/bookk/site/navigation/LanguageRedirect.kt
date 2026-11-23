package me.bookk.site.navigation

import me.bookk.site.designsystem.resources.AppLanguage
import me.bookk.site.legal.LegalDocument

object LanguageRedirect {
    private const val LEGAL_LANGUAGE_SEGMENTS = 3

    fun target(path: String, preferred: AppLanguage): String {
        legalLanguagePath(path)?.let { return it }
        if (AppLanguage.fromPath(path) != null) return path
        return preferred.localize(path)
    }

    private fun legalLanguagePath(path: String): String? {
        val segments = path.removePrefix("/").split('/')
        if (segments.size != LEGAL_LANGUAGE_SEGMENTS) return null
        val (slug, version, tag) = segments
        if (LegalDocument.entries.none { it.slug == slug }) return null
        val language = AppLanguage.entries.firstOrNull { it.tag == tag } ?: return null
        return language.localize("/$slug/$version")
    }
}
