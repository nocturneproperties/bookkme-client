package me.bookk.feature.employees.domain.api

import kotlin.uuid.Uuid

interface IsBusinessOwner {
    suspend operator fun invoke(userId: Uuid, businessId: Uuid): Boolean
    suspend operator fun invoke(businessId: Uuid): Boolean
}
