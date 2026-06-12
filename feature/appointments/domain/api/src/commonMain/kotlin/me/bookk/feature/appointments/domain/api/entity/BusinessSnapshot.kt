package me.bookk.feature.appointments.domain.api.entity

import kotlin.uuid.Uuid

data class BusinessSnapshot(
    val id: Uuid,
    val name: String,
    val address: String,
    val isEnabled: Boolean
) {
    companion object {
        fun stub() = BusinessSnapshot(
            id = Uuid.random(),
            name = "Business name",
            address = "Business address",
            isEnabled = true
        )
    }
}