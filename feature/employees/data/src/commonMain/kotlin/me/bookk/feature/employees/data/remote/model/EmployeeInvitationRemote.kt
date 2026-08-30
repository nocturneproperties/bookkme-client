package me.bookk.feature.employees.data.remote.model

import kotlinx.serialization.Serializable
import me.bookk.feature.employees.domain.api.entity.EmployeeInvitation
import me.bookk.feature.employees.domain.api.entity.EmployeeInvitationStatus
import kotlin.time.Instant
import kotlin.uuid.Uuid

/**
 * Field order is significant: the API speaks protobuf and none of the remote models declare
 * explicit `@ProtoNumber`s, so field numbers are assigned by declaration order.
 */
@Serializable
internal class EmployeeInvitationRemote(
    val id: Uuid,
    val businessId: Uuid,
    val invitedBy: Uuid,
    val email: String,
    val status: EmployeeInvitationStatusRemote,
    val createdAt: Instant
) {
    fun toDomain() = EmployeeInvitation(
        id = id,
        businessId = businessId,
        invitedBy = invitedBy,
        email = email,
        status = status.toDomain(),
        createdAt = createdAt
    )
}

@Serializable
internal enum class EmployeeInvitationStatusRemote {
    PENDING,
    APPROVED;

    fun toDomain() = when (this) {
        PENDING -> EmployeeInvitationStatus.PENDING
        APPROVED -> EmployeeInvitationStatus.APPROVED
    }
}

@Serializable
internal class EmployeeInvitationRequestRemote(
    val email: String
)
