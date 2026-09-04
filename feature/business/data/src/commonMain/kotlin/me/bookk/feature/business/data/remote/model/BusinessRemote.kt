package me.bookk.feature.business.data.remote.model

import kotlinx.datetime.TimeZone
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import me.bookk.core.data.TimeZoneSerializer
import me.bookk.feature.business.domain.api.entity.Business
import me.bookk.feature.business.domain.api.entity.BusinessPermissions
import me.bookk.feature.business.domain.api.entity.ResourcePermission
import kotlin.uuid.Uuid

@Serializable
class BusinessRemote(
    @ProtoNumber(1) val id: Uuid,
    @ProtoNumber(2) val name: String,
    @ProtoNumber(3) val description: String,
    @ProtoNumber(4) val address: String,
    @ProtoNumber(5)
    @Serializable(with = TimeZoneSerializer::class)
    val timeZone: TimeZone,
    @ProtoNumber(6) val location: Location?,
    @ProtoNumber(7) val currencyCode: String,
    @ProtoNumber(8) val socials: List<Social>,
    @ProtoNumber(9) val schedule: ScheduleRemote,
    @ProtoNumber(10) val permissions: BusinessPermissionsRemote
) {
    @Serializable
    class Location(
        @ProtoNumber(1) val lat: Double,
        @ProtoNumber(2) val lng: Double
    )

    @Serializable
    class Social(
        @ProtoNumber(1) val kind: SocialKind,
        @ProtoNumber(2) val value: String?
    )

    enum class SocialKind(val domainKind: Business.SocialKind) {
        PHONE(Business.SocialKind.PHONE),
        INSTAGRAM(Business.SocialKind.INSTAGRAM),
        TELEGRAM(Business.SocialKind.TELEGRAM),
        VIBER(Business.SocialKind.VIBER),
        WHATSAPP(Business.SocialKind.WHATSAPP)
    }
}

@Serializable
class ResourcePermissionRemote(
    @ProtoNumber(1) val view: Boolean = false,
    @ProtoNumber(2) val update: Boolean = false,
    @ProtoNumber(3) val delete: Boolean = false
) {
    fun toDomain() = ResourcePermission(view = view, update = update, delete = delete)
}

@Serializable
class BusinessPermissionsRemote(
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
