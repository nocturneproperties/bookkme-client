package me.bookk.feature.employees.data.remote.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import me.bookk.feature.business.domain.api.entity.BusinessPermissions
import me.bookk.feature.business.domain.api.entity.ResourcePermission
import me.bookk.feature.employees.domain.api.entity.Employee
import me.bookk.feature.employees.domain.api.entity.EmployeeRole
import kotlin.time.Instant
import kotlin.uuid.Uuid

@Serializable
internal class EmployeeRemote(
    @ProtoNumber(1) val id: Uuid,
    @ProtoNumber(2) val businessId: Uuid,
    @ProtoNumber(3) val name: String,
    @ProtoNumber(4) val lastName: String,
    @ProtoNumber(5) val phone: String?,
    @ProtoNumber(6) val email: String?,
    @ProtoNumber(7) val userId: Uuid,
    @ProtoNumber(8) val services: List<ServiceRemote>,
    @ProtoNumber(9) val schedule: ScheduleRemote,
    @ProtoNumber(10) val createdAt: Instant
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
    @ProtoNumber(1) val role: EmployeeRoleRemote
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

@Serializable
internal class ResourcePermissionRemote(
    @ProtoNumber(1) val view: Boolean = false,
    @ProtoNumber(2) val update: Boolean = false,
    @ProtoNumber(3) val delete: Boolean = false
) {
    fun toDomain() = ResourcePermission(view = view, update = update, delete = delete)

    companion object {
        fun fromDomain(permission: ResourcePermission) = ResourcePermissionRemote(
            view = permission.view,
            update = permission.update,
            delete = permission.delete
        )
    }
}

@Serializable
internal class BusinessPermissionsRemote(
    @ProtoNumber(1) val business: ResourcePermissionRemote,
    @ProtoNumber(2) val employees: ResourcePermissionRemote,
    @ProtoNumber(3) val clients: ResourcePermissionRemote,
    @ProtoNumber(4) val services: ResourcePermissionRemote,
    @ProtoNumber(5) val appointments: ResourcePermissionRemote
) {
    fun toDomain() = BusinessPermissions(
        business = business.toDomain(),
        employees = employees.toDomain(),
        clients = clients.toDomain(),
        services = services.toDomain(),
        appointments = appointments.toDomain()
    )
}
