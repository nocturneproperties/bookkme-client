package me.bookk.site.legal

import kotlinx.datetime.LocalDate
import me.bookk.site.designsystem.resources.AppLanguage
import me.bookk.site.legal.markdown.Markdown
import me.bookk.site.legal.markdown.MarkdownBlock
import kotlin.time.Instant

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

object LegalPages {
    fun build(
        history: LegalDocumentHistory,
        request: LegalPageRequest,
        language: AppLanguage,
        now: Instant,
    ): LegalPage? {
        val version = selectVersion(history, request.version, now) ?: return null
        val text = version.text(language) ?: return null
        return LegalPage(
            version = version,
            language = language,
            blocks = Markdown.parse(text),
            notice = notice(history, version, now)?.localized(language),
            languages = languageLinks(version, language),
            history = historyEntries(history, version, now, language),
        )
    }

    fun isPublished(
        history: LegalDocumentHistory,
        request: LegalPageRequest,
        language: AppLanguage,
        now: Instant,
    ): Boolean {
        return selectVersion(history, request.version, now)?.text(language) != null
    }

    private fun selectVersion(
        history: LegalDocumentHistory,
        requested: String?,
        now: Instant,
    ): LegalDocumentVersion? {
        if (requested != null) return history.find(requested)
        return history.inEffect(now) ?: history.next(now)
    }

    private fun notice(
        history: LegalDocumentHistory,
        version: LegalDocumentVersion,
        now: Instant,
    ): LegalNotice? {
        val current = history.inEffect(now)
        return when (val period = period(history, version, now)) {
            is LegalPeriod.Superseded -> current?.let { LegalNotice.Superseded(period, it.path) }

            is LegalPeriod.Upcoming -> LegalNotice.Upcoming(period.from, current?.path)

            is LegalPeriod.InEffect -> history.next(now)?.let {
                LegalNotice.NewVersion(it.effectiveDate, it.path)
            }
        }
    }

    private fun period(
        history: LegalDocumentHistory,
        version: LegalDocumentVersion,
        now: Instant,
    ): LegalPeriod {
        val successor = history.successor(version)
        return when {
            version.effectiveAt > now -> {
                LegalPeriod.Upcoming(version.effectiveDate)
            }

            successor != null && successor.effectiveAt <= now -> {
                LegalPeriod.Superseded(version.effectiveDate, successor.effectiveDate)
            }

            else -> {
                LegalPeriod.InEffect(version.effectiveDate)
            }
        }
    }

    private fun languageLinks(
        version: LegalDocumentVersion,
        selected: AppLanguage,
    ): List<LegalLanguageLink> {
        return AppLanguage.entries.map {
            LegalLanguageLink(
                it,
                it.localize(version.path),
                it == selected,
            )
        }
    }

    private fun historyEntries(
        history: LegalDocumentHistory,
        selected: LegalDocumentVersion,
        now: Instant,
        language: AppLanguage,
    ): List<LegalHistoryEntry> {
        return history.versions.map {
            LegalHistoryEntry(
                it.version,
                language.localize(it.path),
                period(history, it, now),
                it == selected,
            )
        }
    }

    private fun LegalNotice.localized(language: AppLanguage): LegalNotice {
        return when (this) {
            is LegalNotice.Superseded -> copy(currentPath = language.localize(currentPath))
            is LegalNotice.Upcoming -> copy(currentPath = currentPath?.let(language::localize))
            is LegalNotice.NewVersion -> copy(path = language.localize(path))
        }
    }
}
