package me.bookk.feature.employees.data.remote.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import me.bookk.feature.business.domain.api.entity.BusinessPermissions
import me.bookk.feature.business.domain.api.entity.ResourcePermission
import me.bookk.feature.employees.domain.api.entity.Employee
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
    @ProtoNumber(10) val createdAt: Instant,
    @ProtoNumber(11) val permissions: BusinessPermissionsRemote
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
        createdAt = createdAt,
        permissions = permissions.toDomain()
    )
}

@Serializable
internal class EmployeeUpdateRequest(
    @ProtoNumber(1) val id: Uuid,
    @ProtoNumber(2) val businessId: Uuid,
    @ProtoNumber(3) val name: String,
    @ProtoNumber(4) val lastName: String,
    @ProtoNumber(5) val phone: String?,
    @ProtoNumber(6) val email: String?,
    @ProtoNumber(7) val services: List<ServiceRemote>,
    @ProtoNumber(8) val schedule: ScheduleRemote
) {
    companion object {
        fun fromDomain(employee: Employee): EmployeeUpdateRequest {
            return EmployeeUpdateRequest(
                id = employee.id,
                businessId = employee.businessId,
                name = employee.name,
                lastName = employee.lastName,
                phone = employee.phone,
                email = employee.email,
                services = employee.services.map { ServiceRemote.fromDomain(it) },
                schedule = ScheduleRemote.fromDomain(employee.schedule)
            )
        }
    }
}

@Serializable
internal class EmployeePermissionsRequest(
    @ProtoNumber(1) val business: ResourcePermissionRemote? = null,
    @ProtoNumber(2) val employees: ResourcePermissionRemote? = null,
    @ProtoNumber(3) val clients: ResourcePermissionRemote? = null,
    @ProtoNumber(4) val services: ResourcePermissionRemote? = null,
    @ProtoNumber(5) val appointments: ResourcePermissionRemote? = null
) {
    companion object {
        fun fromDomain(permissions: BusinessPermissions): EmployeePermissionsRequest {
            return EmployeePermissionsRequest(
                business = ResourcePermissionRemote.fromDomain(permissions.business),
                employees = ResourcePermissionRemote.fromDomain(permissions.employees),
                clients = ResourcePermissionRemote.fromDomain(permissions.clients),
                services = ResourcePermissionRemote.fromDomain(permissions.services),
                appointments = ResourcePermissionRemote.fromDomain(permissions.appointments)
            )
        }
    }
}

@Serializable
internal class ResourcePermissionRemote(
    @ProtoNumber(1) val view: Boolean,
    @ProtoNumber(2) val update: Boolean,
    @ProtoNumber(3) val delete: Boolean
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
