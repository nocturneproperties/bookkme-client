package me.bookk.feature.employees.domain.api.entity

import me.bookk.feature.business.domain.api.entity.WorkingSchedule
import me.bookk.feature.services.domain.api.service.entity.Service
import kotlin.uuid.Uuid

data class BookableEmployee(
    val id: Uuid,
    val businessId: Uuid,
    val name: String,
    val lastName: String,
    val services: List<Service>,
    val schedule: WorkingSchedule,
)
