package me.bookk.feature.appointments.domain.api.entity

import kotlin.uuid.Uuid

data class CurrentBusiness(
    val id: Uuid,
    val employeeId: Uuid?
)
