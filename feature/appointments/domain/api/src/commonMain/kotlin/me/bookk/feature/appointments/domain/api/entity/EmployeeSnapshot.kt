package me.bookk.feature.appointments.domain.api.entity

import kotlin.uuid.Uuid

data class EmployeeSnapshot(
    val id: Uuid,
    val fullName: String
) {
    companion object {
        fun stub() = EmployeeSnapshot(
            id = Uuid.random(),
            fullName = "John Doe"
        )
    }
}
