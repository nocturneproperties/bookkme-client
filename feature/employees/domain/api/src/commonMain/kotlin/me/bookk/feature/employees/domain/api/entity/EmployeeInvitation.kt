package me.bookk.feature.employees.domain.api.entity

import kotlin.time.Instant
import kotlin.uuid.Uuid

data class EmployeeInvitation(
    val id: Uuid,
    val businessId: Uuid,
    val invitedBy: Uuid,
    val email: String,
    val status: EmployeeInvitationStatus,
    val createdAt: Instant
)

enum class EmployeeInvitationStatus {
    PENDING,
    APPROVED
}
