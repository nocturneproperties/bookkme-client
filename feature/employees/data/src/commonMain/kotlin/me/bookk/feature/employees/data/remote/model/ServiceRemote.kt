package me.bookk.feature.employees.data.remote.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import library.money.api.Money
import library.money.api.RemoteMoneySerializer
import me.bookk.feature.services.domain.api.group.entity.ServiceGroup
import me.bookk.feature.services.domain.api.service.entity.Service
import kotlin.time.Duration
import kotlin.time.Instant
import kotlin.uuid.Uuid

@Serializable
internal class ServiceGroupRemote(
    @ProtoNumber(1) val id: Uuid,
    @ProtoNumber(2) val businessId: Uuid,
    @ProtoNumber(3) val name: String,
    @ProtoNumber(4) val createdAt: Instant
) {
    fun toDomain() = ServiceGroup(
        id = id,
        businessId = businessId,
        name = name,
        createdAt = createdAt
    )

    companion object {
        fun fromDomain(group: ServiceGroup) = ServiceGroupRemote(
            id = group.id,
            businessId = group.businessId,
            name = group.name,
            createdAt = group.createdAt
        )
    }
}

@Serializable
internal class ServiceRemote(
    @ProtoNumber(1) val id: Uuid,
    @ProtoNumber(2) val businessId: Uuid,
    @ProtoNumber(3) val group: ServiceGroupRemote,
    @ProtoNumber(4) val name: String,
    @ProtoNumber(5) val duration: Duration,
    @ProtoNumber(6)
    @Serializable(with = RemoteMoneySerializer::class)
    val price: Money,
    @ProtoNumber(7) val isAvailable: Boolean,
    @ProtoNumber(8) val createdAt: Instant
) {
    fun toDomain() = Service(
        id = id,
        businessId = businessId,
        group = group.toDomain(),
        name = name,
        duration = duration,
        price = price,
        isAvailable = isAvailable,
        createdAt = createdAt
    )

    companion object {
        fun fromDomain(service: Service) = ServiceRemote(
            id = service.id,
            businessId = service.businessId,
            group = ServiceGroupRemote.fromDomain(service.group),
            name = service.name,
            duration = service.duration,
            price = service.price,
            isAvailable = service.isAvailable,
            createdAt = service.createdAt
        )
    }
}
