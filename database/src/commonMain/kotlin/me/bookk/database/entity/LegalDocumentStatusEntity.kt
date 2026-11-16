package me.bookk.database.entity

import androidx.room3.Entity
import androidx.room3.PrimaryKey
import kotlin.time.Instant

@Entity(tableName = "legal_document_status")
data class LegalDocumentStatusEntity(
    @PrimaryKey val document: String,
    val version: String,
    val url: String,
    val effectiveAt: Instant,
    val acceptedAt: Instant?,
)
