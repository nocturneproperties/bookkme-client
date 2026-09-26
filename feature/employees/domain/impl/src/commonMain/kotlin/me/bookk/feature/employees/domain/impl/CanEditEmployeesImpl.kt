package me.bookk.feature.employees.domain.impl

import kotlinx.coroutines.flow.first
import me.bookk.feature.business.domain.api.business.ObserveUserBusinessesChanges
import me.bookk.feature.employees.domain.api.CanEditEmployees
import kotlin.uuid.Uuid

internal class CanEditEmployeesImpl(
    private val observeUserBusinessesChanges: ObserveUserBusinessesChanges
) : CanEditEmployees {
    override suspend fun invoke(businessId: Uuid): Boolean {
        val business = observeUserBusinessesChanges().first().firstOrNull { it.id == businessId }
        return business?.permissions?.employees?.update == true
    }
}
