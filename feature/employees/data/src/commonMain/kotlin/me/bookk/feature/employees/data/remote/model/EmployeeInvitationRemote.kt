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
    @ProtoNumber(4) val code: String?,
    @ProtoNumber(5) val status: EmployeeInvitationStatusRemote,
    @ProtoNumber(6) val createdAt: Instant
) {
    fun toDomain() = EmployeeInvitation(
        id = id,
        businessId = businessId,
        invitedBy = invitedBy,
        code = code,
        status = status.toDomain(),
        createdAt = createdAt.toLocalDateTime(TimeZone.currentSystemDefault())
    )
}

@Serializable
internal enum class EmployeeInvitationStatusRemote {
    PENDING,
    REDEEMED,
    EXPIRED,
    REVOKED;

    fun toDomain() = when (this) {
        PENDING -> EmployeeInvitationStatus.PENDING
        REDEEMED -> EmployeeInvitationStatus.REDEEMED
        EXPIRED -> EmployeeInvitationStatus.EXPIRED
        REVOKED -> EmployeeInvitationStatus.REVOKED
    }
}
