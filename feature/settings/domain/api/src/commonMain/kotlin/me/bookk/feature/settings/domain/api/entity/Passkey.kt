package me.bookk.feature.settings.domain.api.entity

import kotlinx.datetime.LocalDateTime

data class Passkey(
    val id: Long,
    val name: String,
    val isBackedUp: Boolean,
    val createdAt: LocalDateTime,
    val lastUsedAt: LocalDateTime
)