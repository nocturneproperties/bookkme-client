package me.bookk.feature.appointments.domain.api.entity

import kotlin.uuid.Uuid

data class ClientSnapshot(
    val id: Uuid,
    val fullName: String,
    val phone: String,
    val email: String
) {
    companion object {
        fun stub() = ClientSnapshot(
            id = Uuid.random(),
            fullName = "Client Name",
            phone = "123456789",
            email = "client@example.com"
        )
    }
}