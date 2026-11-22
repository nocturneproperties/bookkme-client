package me.bookk.feature.appointments.domain.api.entity

import kotlin.time.Instant
import kotlin.uuid.Uuid

data class SavedBusiness(
    val id: Uuid,
    val name: String,
    val isEnabled: Boolean,
    val savedAt: Instant,
    val lastVisitAt: Instant?,
)

data class SavedBusinessEntry(
    val businessId: Uuid,
    val isEnabled: Boolean,
    val savedAt: Instant,
    val lastVisitAt: Instant?,
)
