package me.bookk.feature.appointments.data.remote.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import library.money.api.Money
import library.money.api.RemoteMoneySerializer
import me.bookk.feature.appointments.domain.api.entity.PriceAdjustment

@Serializable
data class PriceAdjustmentRemote(
    @ProtoNumber(2)
    @Serializable(with = RemoteMoneySerializer::class)
    val price: Money,
    @ProtoNumber(3) val reason: String? = null,
    @ProtoNumber(4) val additionalServices: List<AdditionalServiceRemote> = emptyList(),
) {
    fun toDomain(): PriceAdjustment {
        return PriceAdjustment(
            additionalServices = additionalServices.flatMap { it.toDomainUnits() },
            price = price,
            reason = reason,
        )
    }
}

@Serializable
data class AdditionalServiceRemote(
    @ProtoNumber(1) val service: ServiceSnapshotRemote,
    @ProtoNumber(2) val count: Int,
) {
    fun toDomainUnits() = List(count) { service.toDomain() }
}

@Serializable
data class PriceAdjustmentDraftRemote(
    @ProtoNumber(2)
    @Serializable(with = RemoteMoneySerializer::class)
    val price: Money,
    @ProtoNumber(3) val reason: String?,
    @ProtoNumber(4) val additionalServices: List<RequestedServiceRemote>,
)

@Serializable
data class CompleteAppointmentRequestRemote(
    @ProtoNumber(1) val priceAdjustment: PriceAdjustmentDraftRemote?,
)
