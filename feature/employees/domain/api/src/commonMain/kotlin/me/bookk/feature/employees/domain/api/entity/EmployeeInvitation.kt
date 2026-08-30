package me.bookk.feature.employees.domain.api.entity

import kotlinx.datetime.LocalDateTime
import kotlin.uuid.Uuid

data class EmployeeInvitation(
    val id: Uuid,
    val businessId: Uuid,
    val invitedBy: Uuid,
    val email: String,
    val status: EmployeeInvitationStatus,
    val createdAt: LocalDateTime
)

enum class EmployeeInvitationStatus {
    PENDING,
    APPROVED,
    REJECTED,
    EXPIRED,
    REVOKED
}
