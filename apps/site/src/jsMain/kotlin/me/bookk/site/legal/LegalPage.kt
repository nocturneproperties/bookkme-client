package me.bookk.site.legal

import kotlinx.datetime.LocalDate
import me.bookk.designsystem.resources.AppLanguage
import me.bookk.site.legal.markdown.MarkdownBlock

class LegalPageRequest(
    val version: String? = null,
)

class LegalPage(
    val version: LegalDocumentVersion,
    val language: AppLanguage,
    val blocks: List<MarkdownBlock>,
    val notice: LegalNotice?,
    val languages: List<LegalLanguageLink>,
    val history: List<LegalHistoryEntry>,
)

data class LegalLanguageLink(
    val language: AppLanguage,
    val path: String,
    val isSelected: Boolean,
)

data class LegalHistoryEntry(
    val version: String,
    val path: String,
    val period: LegalPeriod,
    val isSelected: Boolean,
)

sealed interface LegalPeriod {
    data class InEffect(
        val since: LocalDate,
    ) : LegalPeriod

    data class Superseded(
        val from: LocalDate,
        val until: LocalDate,
    ) : LegalPeriod

    data class Upcoming(
        val from: LocalDate,
    ) : LegalPeriod
}

sealed interface LegalNotice {
    data class Superseded(
        val period: LegalPeriod.Superseded,
        val currentPath: String,
    ) : LegalNotice

    data class Upcoming(
        val from: LocalDate,
        val currentPath: String?,
    ) : LegalNotice

    data class NewVersion(
        val from: LocalDate,
        val path: String,
    ) : LegalNotice
}
