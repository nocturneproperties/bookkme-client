package me.bookk.feature.employees.domain.api

import kotlin.uuid.Uuid

interface CanEditEmployees {
    suspend operator fun invoke(businessId: Uuid): Boolean
}
