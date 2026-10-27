package me.bookk.feature.settings.domain.api.entity

import kotlinx.datetime.LocalDateTime
import kotlin.uuid.Uuid

data class Passkey(
    val id: Uuid,
    val name: String,
    val isBackedUp: Boolean,
    val createdAt: LocalDateTime,
    val lastUsedAt: LocalDateTime
)