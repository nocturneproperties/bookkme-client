package me.bookk.feature.appointments.data.remote.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import library.money.api.Money
import library.money.api.RemoteMoneySerializer
import me.bookk.feature.appointments.domain.api.entity.ServiceSnapshot
import kotlin.time.Duration
import kotlin.uuid.Uuid

@Serializable
data class ServiceSnapshotRemote(
    @ProtoNumber(1) val id: Uuid,
    @ProtoNumber(2) val name: String,
    @ProtoNumber(3) val groupId: Uuid,
    @ProtoNumber(4)
    @Serializable(with = RemoteMoneySerializer::class)
    val price: Money,
    @ProtoNumber(5) val duration: Duration
) {
    fun toDomain() = ServiceSnapshot(
        id = id,
        name = name,
        groupId = groupId,
        price = price,
        duration = duration
    )
}
