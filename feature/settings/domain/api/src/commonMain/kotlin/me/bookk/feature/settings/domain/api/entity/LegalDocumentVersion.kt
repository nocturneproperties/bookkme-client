package me.bookk.feature.settings.domain.api.entity

import kotlin.time.Instant

data class LegalDocumentVersion(
    val document: LegalDocument,
    val version: String,
    val url: String,
    val effectiveAt: Instant,
)
