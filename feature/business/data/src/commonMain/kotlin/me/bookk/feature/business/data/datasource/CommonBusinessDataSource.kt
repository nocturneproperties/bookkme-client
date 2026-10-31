package me.bookk.feature.business.data.datasource

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.resources.get
import io.ktor.client.plugins.resources.post
import io.ktor.client.plugins.resources.put
import io.ktor.client.request.setBody
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.datetime.TimeZone
import me.bookk.core.data.DataSource
import me.bookk.core.domain.logout.LogOutAction
import me.bookk.database.dao.BusinessDao
import me.bookk.feature.business.data.mapping.toDayOffEntities
import me.bookk.feature.business.data.mapping.toDayScheduleEntities
import me.bookk.feature.business.data.mapping.toDomain
import me.bookk.feature.business.data.mapping.toLocal
import me.bookk.feature.business.data.mapping.toRemote
import me.bookk.feature.business.data.mapping.toUserBusinesses
import me.bookk.feature.business.data.mapping.toWorkHourEntities
import me.bookk.feature.business.data.remote.api.BusinessRouting
import me.bookk.feature.business.data.remote.model.BusinessRemote
import me.bookk.feature.business.data.remote.model.CreateBusinessRequest
import me.bookk.feature.business.data.remote.model.UserBusinessesRemote
import me.bookk.feature.business.domain.api.entity.Business
import me.bookk.feature.business.domain.api.entity.UserBusinessInfo
import me.bookk.feature.business.domain.datasource.BusinessDataSource
import kotlin.uuid.Uuid

internal class CommonBusinessDataSource(
    private val httpClient: HttpClient,
    private val businessDao: BusinessDao
) : DataSource(), BusinessDataSource, LogOutAction {

    override suspend fun createBusiness(name: String, currencyCode: String, timeZone: TimeZone): Business =
        mapExceptions {
            httpClient.post(BusinessRouting.Api.Business()) {
                setBody(CreateBusinessRequest(name, currencyCode, timeZone))
            }
                .body<BusinessRemote>()
                .toDomain()
        }

    override suspend fun updateBusiness(business: Business) {
        mapExceptions {
            httpClient.put(BusinessRouting.Api.Business.Id(id = business.id)) {
                setBody(business.toRemote())
            }
        }
    }

    override suspend fun clearBusinessTable() = mapExceptions {
        businessDao.clear()
    }

    override suspend fun saveBusinessInDB(business: Business) {
        mapExceptions {
            businessDao.upsertWithChildren(
                business = business.toLocal(),
                daySchedules = business.toDayScheduleEntities(),
                workHours = business.toWorkHourEntities(),
                dayOffs = business.toDayOffEntities()
            )
        }
    }

    override suspend fun saveBusinessListInDB(businesses: List<Business>) {
        mapExceptions {
            businessDao.upsertAllWithChildren(
                businesses = businesses.map { it.toLocal() },
                daySchedules = businesses.flatMap { it.toDayScheduleEntities() },
                workHours = businesses.flatMap { it.toWorkHourEntities() },
                dayOffs = businesses.flatMap { it.toDayOffEntities() }
            )
        }
    }

    override suspend fun saveBusinessListWithDashboardInDB(businesses: List<Business>, dashboardBusinessId: Uuid?) {
        mapExceptions {
            businessDao.upsertAllWithChildrenAndDashboard(
                businesses = businesses.map { it.toLocal() },
                daySchedules = businesses.flatMap { it.toDayScheduleEntities() },
                workHours = businesses.flatMap { it.toWorkHourEntities() },
                dayOffs = businesses.flatMap { it.toDayOffEntities() },
                dashboardBusinessId = dashboardBusinessId
            )
        }
    }

    override suspend fun getBusinessIdsInDb(): List<Uuid> = mapExceptions {
        businessDao.getIds()
    }

    override suspend fun deleteBusinessesInDb(ids: List<Uuid>) {
        mapExceptions {
            ids.chunked(DELETE_CHUNK_SIZE).forEach { chunk -> businessDao.deleteByIds(chunk) }
        }
    }

    override fun observeBusinessDBChanges(businessId: Uuid): Flow<Business?> {
        return businessDao.observeBusiness(businessId)
            .map { it?.toDomain() }
            .mapErrors()
    }

    override fun observeAllBusinessesInDb(): Flow<List<Business>> {
        return businessDao.observeAllBusinesses()
            .map { businesses -> businesses.map { it.toDomain() } }
            .mapErrors()
    }

    override fun observeUserBusinessInfoInDb(): Flow<UserBusinessInfo> {
        return businessDao.observeAllBusinessesWithDashboardFlag()
            .map { rows ->
                UserBusinessInfo(
                    dashboardId = rows.firstOrNull { it.isDashboard }?.business?.entity?.id,
                    businesses = rows.map { it.business.toDomain() }
                )
            }
            .mapErrors()
    }

    override suspend fun setDashboardBusinessOnRemote(businessId: Uuid) {
        mapExceptions {
            httpClient.put(BusinessRouting.Api.Business.Id.Dashboard(parent = BusinessRouting.Api.Business.Id(id = businessId)))
        }
    }

    override suspend fun getBusinessesFromRemote(): UserBusinessInfo = mapExceptions {
        httpClient.get(BusinessRouting.Api.Business.Mine())
            .body<UserBusinessesRemote>()
            .toUserBusinesses()
    }

    override suspend fun getBusinessById(id: Uuid): Business? {
        return businessDao.queryBusiness(id)?.toDomain()
    }

    override suspend fun saveDashboardBusinessId(id: Uuid?) {
        mapExceptions {
            businessDao.setDashboardBusiness(id)
        }
    }

    override fun getDashboardBusinessIdFlow(): Flow<Uuid?> {
        return businessDao.observeDashboardBusinessId()
            .distinctUntilChanged()
            .mapErrors()
    }

    override suspend fun doOnLogOut() {
        businessDao.clearDashboardBusiness()
        businessDao.clear()
    }
}