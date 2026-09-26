package me.bookk.feature.services.domain.impl

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.bookk.feature.business.domain.api.business.ObserveDashboardBusinessChanges
import me.bookk.feature.services.domain.api.ObserveCurrentBusinessId
import kotlin.uuid.Uuid

internal class ObserveCurrentBusinessIdImpl(
    private val observeDashboardBusinessChanges: ObserveDashboardBusinessChanges
) : ObserveCurrentBusinessId {
    override fun invoke(): Flow<Uuid?> {
        return observeDashboardBusinessChanges.invoke()
            .map { it?.id }
    }
}
