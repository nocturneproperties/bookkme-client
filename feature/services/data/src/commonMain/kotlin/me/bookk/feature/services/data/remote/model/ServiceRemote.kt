package me.bookk.feature.services.data.remote.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import library.money.api.Money
import library.money.api.RemoteMoneySerializer
import me.bookk.feature.services.domain.api.service.entity.Service
import kotlin.time.Duration
import kotlin.time.Instant
import kotlin.uuid.Uuid

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
    fun toDomain(): Service {
        return Service(
            id = id,
            businessId = businessId,
            group = group.toDomain(),
            name = name,
            duration = duration,
            price = price,
            isAvailable = isAvailable,
            createdAt = createdAt
        )
    }

    companion object {
        fun fromDomain(service: Service): ServiceRemote = with(service) {
            return ServiceRemote(
                id = id,
                businessId = businessId,
                group = ServiceGroupRemote.fromDomain(group),
                name = name,
                duration = duration,
                price = price,
                isAvailable = isAvailable,
                createdAt = createdAt
            )
        }
    }
}
