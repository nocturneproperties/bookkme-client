package me.bookk.feature.employees.data.remote.model

import kotlinx.serialization.Serializable
import me.bookk.feature.employees.domain.api.entity.Employee
import me.bookk.feature.employees.domain.api.entity.EmployeeRole
import kotlin.time.Instant
import kotlin.uuid.Uuid

/**
 * Field order is significant: the API speaks protobuf and none of the remote models declare
 * explicit `@ProtoNumber`s, so field numbers are assigned by declaration order.
 */
@Serializable
internal class EmployeeRemote(
    val id: Uuid,
    val businessId: Uuid,
    val name: String,
    val lastName: String,
    val phone: String?,
    val email: String?,
    val userId: Uuid,
    val services: List<ServiceRemote>,
    val schedule: ScheduleRemote,
    val createdAt: Instant
) {
    fun toDomain() = Employee(
        id = id,
        businessId = businessId,
        name = name,
        lastName = lastName,
        phone = phone,
        email = email,
        userId = userId,
        services = services.map { it.toDomain() },
        schedule = schedule.toDomain(),
        createdAt = createdAt
    )

    companion object {
        fun fromDomain(employee: Employee) = EmployeeRemote(
            id = employee.id,
            businessId = employee.businessId,
            name = employee.name,
            lastName = employee.lastName,
            phone = employee.phone,
            email = employee.email,
            userId = employee.userId,
            services = employee.services.map { ServiceRemote.fromDomain(it) },
            schedule = ScheduleRemote.fromDomain(employee.schedule),
            createdAt = employee.createdAt
        )
    }
}

@Serializable
internal class PromoteEmployeeRequestRemote(
    val role: EmployeeRoleRemote
)

@Serializable
internal enum class EmployeeRoleRemote {
    EMPLOYEE,
    MANAGER;

    fun toDomain() = when (this) {
        EMPLOYEE -> EmployeeRole.EMPLOYEE
        MANAGER -> EmployeeRole.MANAGER
    }

    companion object {
        fun fromDomain(role: EmployeeRole) = when (role) {
            EmployeeRole.EMPLOYEE -> EMPLOYEE
            EmployeeRole.MANAGER -> MANAGER
        }
    }
}
