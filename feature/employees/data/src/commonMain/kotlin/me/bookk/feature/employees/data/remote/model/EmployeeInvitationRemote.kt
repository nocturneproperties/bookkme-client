package me.bookk.feature.employees.data.remote.model

import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import me.bookk.feature.employees.domain.api.entity.EmployeeInvitation
import me.bookk.feature.employees.domain.api.entity.EmployeeInvitationStatus
import kotlin.time.Instant
import kotlin.uuid.Uuid

@Serializable
internal class EmployeeInvitationRemote(
    @ProtoNumber(1) val id: Uuid,
    @ProtoNumber(2) val businessId: Uuid,
    @ProtoNumber(3) val invitedBy: Uuid,
    @ProtoNumber(4) val email: String,
    @ProtoNumber(5) val status: EmployeeInvitationStatusRemote,
    @ProtoNumber(6) val createdAt: Instant
) {
    fun toDomain() = EmployeeInvitation(
        id = id,
        businessId = businessId,
        invitedBy = invitedBy,
        email = email,
        status = status.toDomain(),
        createdAt = createdAt.toLocalDateTime(TimeZone.currentSystemDefault())
    )
}

@Serializable
internal enum class EmployeeInvitationStatusRemote {
    PENDING,
    APPROVED,
    REJECTED,
    EXPIRED,
    REVOKED;

    fun toDomain() = when (this) {
        PENDING -> EmployeeInvitationStatus.PENDING
        APPROVED -> EmployeeInvitationStatus.APPROVED
        REJECTED -> EmployeeInvitationStatus.REJECTED
        EXPIRED -> EmployeeInvitationStatus.EXPIRED
        REVOKED -> EmployeeInvitationStatus.REVOKED
    }
}

@Serializable
internal class EmployeeInvitationRequestRemote(
    @ProtoNumber(1) val email: String
)
