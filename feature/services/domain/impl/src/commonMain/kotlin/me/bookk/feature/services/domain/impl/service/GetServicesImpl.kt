package me.bookk.feature.services.domain.impl.service

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.bookk.core.coroutine.flatMapLatestOrNull
import me.bookk.core.domain.fetch.BusinessDataFetchAction
import me.bookk.feature.business.domain.api.business.ObserveDashboardBusinessChanges
import me.bookk.feature.services.domain.api.service.GetBusinessServices
import me.bookk.feature.services.domain.api.service.GetServices
import me.bookk.feature.services.domain.api.service.entity.Service
import kotlin.uuid.Uuid

internal class GetServicesImpl(
    private val getBusinessServices: GetBusinessServices,
    private val observeDashboardBusinessChanges: ObserveDashboardBusinessChanges,
) : GetServices,
    BusinessDataFetchAction {

    override fun flow(): Flow<List<Service>> {
        return observeDashboardBusinessChanges()
            .flatMapLatestOrNull { business -> getBusinessServices.flow(business.id) }
            .map { it.orEmpty() }
    }

    override suspend fun refresh(businessId: Uuid): List<Service> {
        return getBusinessServices.refresh(businessId)
    }

    override suspend fun doOnBusinessDataFetch(businessId: Uuid) {
        refresh(businessId)
    }
}
