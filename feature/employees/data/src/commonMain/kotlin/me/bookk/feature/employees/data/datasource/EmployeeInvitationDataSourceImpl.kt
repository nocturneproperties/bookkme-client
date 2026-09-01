package me.bookk.feature.employees.data.datasource

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.resources.get
import io.ktor.client.plugins.resources.post
import io.ktor.client.request.setBody
import library.cache.api.PreferenceProvider
import library.cache.api.Preferences
import library.cache.api.get
import library.cache.api.set
import me.bookk.core.data.DataSource
import me.bookk.core.domain.logout.LogOutAction
import me.bookk.database.dao.EmployeeInvitationDao
import me.bookk.feature.employees.data.mapping.toDbEntity
import me.bookk.feature.employees.data.mapping.toDomain
import me.bookk.feature.employees.data.remote.api.EmployeeRouting.Api
import me.bookk.feature.employees.data.remote.model.EmployeeInvitationRemote
import me.bookk.feature.employees.data.remote.model.EmployeeInvitationRequestRemote
import me.bookk.feature.employees.data.remote.model.EmployeeRemote
import me.bookk.feature.employees.domain.api.entity.Employee
import me.bookk.feature.employees.domain.api.entity.EmployeeInvitation
import me.bookk.feature.employees.domain.datasource.EmployeeInvitationDataSource
import kotlin.time.Clock
import kotlin.time.Instant
import kotlin.uuid.Uuid

internal class EmployeeInvitationDataSourceImpl(
    private val httpClient: HttpClient,
    private val employeeInvitationDao: EmployeeInvitationDao,
    preferenceProvider: PreferenceProvider
) : DataSource(), EmployeeInvitationDataSource, LogOutAction {

    private val preferences = preferenceProvider.get("employee_invitations_prefs")
    override suspend fun createInvitation(businessId: Uuid, email: String): EmployeeInvitation = mapExceptions {
        httpClient.post(Api.EmployeeInvitation(businessId = businessId)) {
            setBody(EmployeeInvitationRequestRemote(email = email))
        }
            .body<EmployeeInvitationRemote>()
            .toDomain()
    }

    override suspend fun getInvitations(businessId: Uuid): List<EmployeeInvitation> = mapExceptions {
        httpClient.get(Api.EmployeeInvitation(businessId = businessId))
            .body<List<EmployeeInvitationRemote>>()
            .map { it.toDomain() }
    }

    override suspend fun getInvitationsFromDb(businessId: Uuid): List<EmployeeInvitation> = mapExceptions {
        employeeInvitationDao.getInvitations(businessId).map { it.toDomain() }
    }

    override suspend fun saveInvitationsInDb(invitations: List<EmployeeInvitation>) {
        mapExceptions { employeeInvitationDao.upsert(invitations.map(EmployeeInvitation::toDbEntity)) }
    }

    override suspend fun deleteInvitationsInDb() {
        mapExceptions { employeeInvitationDao.clear() }
    }

    override suspend fun getLastSyncedAt(businessId: Uuid): Instant? {
        return preferences.get(Key.lastSyncedAt(businessId))?.let { Instant.fromEpochMilliseconds(it) }
    }

    override suspend fun saveLastSyncedAt(businessId: Uuid) {
        preferences.set(Key.lastSyncedAt(businessId), Clock.System.now().toEpochMilliseconds())
    }

    override suspend fun doOnLogOut() {
        preferences.clear()
        employeeInvitationDao.clear()
    }

    private object Key {
        fun lastSyncedAt(businessId: Uuid) = Preferences.Key<Long>("last_synced_at_$businessId")
    }

    override suspend fun approveInvitation(businessId: Uuid, id: Uuid): Employee = mapExceptions {
        httpClient.post(Api.EmployeeInvitation.Approve(Api.EmployeeInvitation(businessId = businessId), id))
            .body<EmployeeRemote>()
            .toDomain()
    }

    override suspend fun rejectInvitation(businessId: Uuid, id: Uuid) {
        mapExceptions {
            httpClient.post(Api.EmployeeInvitation.Reject(Api.EmployeeInvitation(businessId = businessId), id))
        }
    }

    override suspend fun revokeInvitation(businessId: Uuid, id: Uuid) {
        mapExceptions {
            httpClient.post(Api.EmployeeInvitation.Revoke(Api.EmployeeInvitation(businessId = businessId), id))
        }
    }

    override suspend fun getPendingInvitationsForEmail(email: String): List<EmployeeInvitation> = mapExceptions {
        httpClient.post(Api.PendingEmployeeInvitation()) {
            setBody(EmployeeInvitationRequestRemote(email = email))
        }
            .body<List<EmployeeInvitationRemote>>()
            .map { it.toDomain() }
    }
}
