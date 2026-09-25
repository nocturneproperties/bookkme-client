package me.bookk.feature.employees.domain.impl

import kotlinx.coroutines.flow.first
import me.bookk.feature.business.domain.api.business.ObserveUserBusinessesChanges
import me.bookk.feature.employees.domain.api.IsBusinessOwner
import me.bookk.feature.employees.domain.api.entity.Employee

internal class IsBusinessOwnerImpl(
    private val observeUserBusinessesChanges: ObserveUserBusinessesChanges
) : IsBusinessOwner {
    override suspend fun invoke(employee: Employee): Boolean {
        val business = observeUserBusinessesChanges().first().firstOrNull { it.id == employee.businessId }
        return business?.ownerId == employee.userId
    }
}
