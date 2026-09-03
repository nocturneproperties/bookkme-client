package me.bookk.feature.employees.domain.api.entity

import kotlinx.datetime.LocalDateTime
import kotlin.uuid.Uuid

data class EmployeeInvitation(
    val id: Uuid,
    val businessId: Uuid,
    val invitedBy: Uuid,
    val code: String?,
    val status: EmployeeInvitationStatus,
    val createdAt: LocalDateTime
)

enum class EmployeeInvitationStatus {
    PENDING,
    REDEEMED,
    EXPIRED,
    REVOKED
}
