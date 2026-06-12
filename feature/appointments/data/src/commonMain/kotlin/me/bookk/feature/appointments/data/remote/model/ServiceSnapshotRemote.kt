package me.bookk.feature.appointments.data.remote.model

import kotlinx.serialization.Serializable
import library.money.api.Money
import library.money.api.RemoteMoneySerializer
import me.bookk.feature.appointments.domain.api.entity.ServiceSnapshot
import kotlin.time.Duration
import kotlin.uuid.Uuid

@Serializable
data class ServiceSnapshotRemote(
    val id: Uuid,
    val name: String,
    val groupId: Uuid,
    @Serializable(with = RemoteMoneySerializer::class)
    val price: Money,
    val duration: Duration
) {
    fun toDomain() = ServiceSnapshot(
        id = id,
        name = name,
        groupId = groupId,
        price = price,
        duration = duration
    )
}