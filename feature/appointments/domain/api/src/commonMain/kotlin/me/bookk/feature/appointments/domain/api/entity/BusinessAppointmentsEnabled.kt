package me.bookk.feature.appointments.domain.api.entity

import kotlin.uuid.Uuid

data class BusinessAppointmentsEnabled(val businessId: Uuid, val enabled: Boolean)
