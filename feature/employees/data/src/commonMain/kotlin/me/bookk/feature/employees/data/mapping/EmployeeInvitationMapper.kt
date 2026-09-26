package me.bookk.feature.employees.data.mapping

import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import me.bookk.database.entity.EmployeeInvitationEntity
import me.bookk.feature.employees.domain.api.entity.EmployeeInvitation
import me.bookk.feature.employees.domain.api.entity.EmployeeInvitationStatus

internal fun EmployeeInvitation.toDbEntity(): EmployeeInvitationEntity {
    return EmployeeInvitationEntity(
        id = id,
        businessId = businessId,
        invitedBy = invitedBy,
        code = code,
        status = status.name,
        createdAt = createdAt.toInstant(TimeZone.currentSystemDefault())
    )
}

internal fun EmployeeInvitationEntity.toDomain(): EmployeeInvitation {
    return EmployeeInvitation(
        id = id,
        businessId = businessId,
        invitedBy = invitedBy,
        code = code,
        status = EmployeeInvitationStatus.valueOf(status),
        createdAt = createdAt.toLocalDateTime(TimeZone.currentSystemDefault())
    )
}
