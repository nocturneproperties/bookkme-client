package me.bookk.feature.appointments.data.remote.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import library.money.api.Money
import library.money.api.RemoteMoneySerializer
import me.bookk.feature.appointments.domain.api.entity.PriceAdjustment
import kotlin.uuid.Uuid

@Serializable
data class PriceAdjustmentRemote(
    @ProtoNumber(1) val additionalServices: List<ServiceSnapshotRemote>,
    @ProtoNumber(2)
    @Serializable(with = RemoteMoneySerializer::class)
    val price: Money,
    @ProtoNumber(3) val reason: String? = null
) {
    fun toDomain(): PriceAdjustment {
        return PriceAdjustment(
            additionalServices = additionalServices.map { it.toDomain() },
            price = price,
            reason = reason
        )
    }
}

@Serializable
data class PriceAdjustmentDraftRemote(
    @ProtoNumber(1) val additionalServiceIds: List<Uuid>,
    @ProtoNumber(2)
    @Serializable(with = RemoteMoneySerializer::class)
    val price: Money,
    @ProtoNumber(3) val reason: String?
)

@Serializable
data class CompleteAppointmentRequestRemote(
    @ProtoNumber(1) val priceAdjustment: PriceAdjustmentDraftRemote?
)
