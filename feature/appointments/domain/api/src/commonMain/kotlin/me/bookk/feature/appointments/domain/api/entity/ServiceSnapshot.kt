package me.bookk.feature.appointments.domain.api.entity

import library.money.api.Money
import kotlin.time.Duration
import kotlin.uuid.Uuid

data class ServiceSnapshot(
    val id: Uuid,
    val name: String,
    val groupId: Uuid,
    val price: Money,
    val duration: Duration
) {
    companion object {
        fun stub() = ServiceSnapshot(
            id = Uuid.random(),
            name = "Service Name",
            groupId = Uuid.random(),
            price = Money(100.0, Money.SupportedCurrency.USD),
            duration = Duration.parse("30m")
        )
    }
}