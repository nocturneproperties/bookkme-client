package me.bookk.site.legal

import me.bookk.site.designsystem.resources.AppLanguage
import kotlin.time.Instant

class LegalDocumentHistory private constructor(
    val document: LegalDocument,
    val versions: List<LegalDocumentVersion>,
) {
    fun find(version: String): LegalDocumentVersion? {
        return versions.firstOrNull { it.version == version }
    }

    fun inEffect(now: Instant): LegalDocumentVersion? {
        return versions.firstOrNull { it.effectiveAt <= now }
    }

    fun next(now: Instant): LegalDocumentVersion? {
        return versions.lastOrNull { it.effectiveAt > now }
    }

    fun successor(version: LegalDocumentVersion): LegalDocumentVersion? {
        return versions.getOrNull(versions.indexOf(version) - 1)
    }

    companion object {
        fun from(document: LegalDocument, texts: List<PublishedLegalText>): LegalDocumentHistory {
            val versions = texts
                .filter { it.document == document.slug }
                .groupBy { it.version }
                .map { (version, languages) -> languages.toVersion(document, version) }
                .sortedByDescending { it.effectiveAt }
            return LegalDocumentHistory(document, versions)
        }
    }
}

private fun List<PublishedLegalText>.toVersion(
    document: LegalDocument,
    version: String,
): LegalDocumentVersion {
    return LegalDocumentVersion(
        document,
        version,
        Instant.parse(first().effectiveAt),
        textsByLanguage(),
    )
}

private fun List<PublishedLegalText>.textsByLanguage(): Map<AppLanguage, String> {
    return mapNotNull { text ->
        AppLanguage.entries
            .firstOrNull { it.tag == text.language }
            ?.let { it to text.markdown }
    }.toMap()
}
