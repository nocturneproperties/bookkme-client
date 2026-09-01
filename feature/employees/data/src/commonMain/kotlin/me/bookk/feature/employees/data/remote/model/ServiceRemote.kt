package me.bookk.feature.employees.data.remote.model

import kotlinx.serialization.Serializable
import library.money.api.Money
import library.money.api.RemoteMoneySerializer
import me.bookk.feature.services.domain.api.group.entity.ServiceGroup
import me.bookk.feature.services.domain.api.service.entity.Service
import kotlin.time.Duration
import kotlin.time.Instant
import kotlin.uuid.Uuid

@Serializable
internal class ServiceGroupRemote(
    val id: Uuid,
    val businessId: Uuid,
    val name: String,
    val createdAt: Instant
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
    val id: Uuid,
    val businessId: Uuid,
    val group: ServiceGroupRemote,
    val name: String,
    val duration: Duration,
    @Serializable(with = RemoteMoneySerializer::class)
    val price: Money,
    val isAvailable: Boolean,
    val createdAt: Instant
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
