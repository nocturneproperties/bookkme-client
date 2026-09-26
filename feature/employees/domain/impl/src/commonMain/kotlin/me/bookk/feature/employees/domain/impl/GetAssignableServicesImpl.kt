package me.bookk.feature.employees.domain.impl

import kotlinx.coroutines.flow.Flow
import me.bookk.feature.employees.domain.api.GetAssignableServices
import me.bookk.feature.services.domain.api.service.GetServices
import me.bookk.feature.services.domain.api.service.entity.Service
import kotlin.uuid.Uuid

internal class GetAssignableServicesImpl(
    private val getServices: GetServices
) : GetAssignableServices {
    override fun flow(): Flow<List<Service>> {
        return getServices.flow()
    }

    override suspend fun refresh(businessId: Uuid): List<Service> {
        return getServices.refresh(businessId)
    }
}
