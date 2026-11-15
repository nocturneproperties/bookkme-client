package me.bookk.site.legal

import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import me.bookk.site.designsystem.resources.AppLanguage
import kotlin.time.Instant

class LegalDocumentVersion(
    val document: LegalDocument,
    val version: String,
    val effectiveAt: Instant,
    private val texts: Map<AppLanguage, String>,
) {
    val path: String
        get() = "${document.path}/$version"

    val effectiveDate: LocalDate
        get() = effectiveAt.toLocalDateTime(TimeZone.UTC).date

    fun path(language: AppLanguage): String {
        return "$path/${language.tag}"
    }

    fun text(language: AppLanguage): String? {
        return texts[language]
    }
}
