package me.bookk.feature.authorization.domain.entity

import kotlin.time.Instant
import kotlin.uuid.Uuid

data class EmailVerification(
    val id: Uuid,
    val expiresAt: Instant,
    val resendAvailableAt: Instant,
)
