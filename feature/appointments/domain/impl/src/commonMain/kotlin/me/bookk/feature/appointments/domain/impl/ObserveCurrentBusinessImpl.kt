package me.bookk.feature.appointments.domain.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.bookk.feature.appointments.domain.api.ObserveCurrentBusiness
import me.bookk.feature.appointments.domain.api.entity.CurrentBusiness
import me.bookk.feature.business.domain.api.business.ObserveDashboardBusinessChanges

internal class ObserveCurrentBusinessImpl(
    private val observeDashboardBusinessChanges: ObserveDashboardBusinessChanges
) : ObserveCurrentBusiness {
    override fun invoke(): Flow<CurrentBusiness?> {
        return observeDashboardBusinessChanges()
            .map { business -> business?.let { CurrentBusiness(id = it.id, employeeId = it.employeeId) } }
    }
}
