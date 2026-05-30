package me.bookk.feature.services.data.remote.model

import kotlinx.serialization.Serializable
import library.money.api.Money
import library.money.api.RemoteMoneySerializer
import me.bookk.feature.services.domain.api.service.entity.Service
import kotlin.time.Duration
import kotlin.uuid.Uuid

@Serializable
internal class ServiceRemote(
    val id: Uuid,
    val businessId: Uuid,
    val group: ServiceGroupRemote,
    val name: String,
    val duration: Duration,
    @Serializable(with = RemoteMoneySerializer::class)
    val price: Money,
    val isAvailable: Boolean
) {
    fun toDomain(): Service {
        return Service(
            id = id,
            businessId = businessId,
            group = group.toDomain(),
            name = name,
            duration = duration,
            price = price,
            isAvailable = isAvailable
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
                isAvailable = isAvailable
            )
        }
    }
}