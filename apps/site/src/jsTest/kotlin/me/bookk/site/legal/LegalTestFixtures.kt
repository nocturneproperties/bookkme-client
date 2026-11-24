package me.bookk.site.legal

import kotlinx.datetime.LocalDate
import me.bookk.designsystem.resources.AppLanguage
import kotlin.time.Instant

internal const val FIRST_VERSION = "2026-12-01"
internal const val SECOND_VERSION = "2027-03-01"

internal fun stubPublishedTexts(
    document: LegalDocument = LegalDocument.PRIVACY_POLICY,
    version: String,
    effectiveAt: String = "${version}T00:00:00Z",
): List<PublishedLegalText> {
    return AppLanguage.entries.map {
        PublishedLegalText(document.slug, version, effectiveAt, it.tag, "# ${it.tag} $version")
    }
}

internal fun stubHistory(vararg versions: String): LegalDocumentHistory {
    return LegalDocumentHistory.from(
        LegalDocument.PRIVACY_POLICY,
        versions.flatMap {
            stubPublishedTexts(version = it)
        },
    )
}

internal fun instant(value: String): Instant {
    return Instant.parse(value)
}

internal fun date(value: String): LocalDate {
    return LocalDate.parse(value)
}
