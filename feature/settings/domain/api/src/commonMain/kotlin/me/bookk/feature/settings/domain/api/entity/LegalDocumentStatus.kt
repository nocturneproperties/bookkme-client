package me.bookk.feature.settings.domain.api.entity

import kotlin.time.Instant

data class LegalDocumentStatus(
    val current: LegalDocumentVersion,
    val acceptedAt: Instant?,
)
