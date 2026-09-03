package me.bookk.feature.clients.data.datasource

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.resources.delete
import io.ktor.client.plugins.resources.get
import io.ktor.client.plugins.resources.patch
import io.ktor.client.plugins.resources.post
import io.ktor.client.request.setBody
import library.cache.api.PreferenceProvider
import library.cache.api.Preferences
import library.cache.api.get
import library.cache.api.set
import me.bookk.core.data.DataSource
import me.bookk.core.domain.logout.LogOutAction
import me.bookk.database.dao.ClientsDao
import me.bookk.feature.clients.data.mapping.toDbEntity
import me.bookk.feature.clients.data.mapping.toDomain
import me.bookk.feature.clients.data.mapping.toRemote
import me.bookk.feature.clients.data.mapping.toUpdateRemote
import me.bookk.feature.clients.data.remote.api.ClientsRouting.Api
import me.bookk.feature.clients.data.remote.model.ClientRemote
import me.bookk.feature.clients.domain.api.entity.Client
import me.bookk.feature.clients.domain.datasource.ClientsDataSource
import kotlin.time.Clock
import kotlin.time.Instant
import kotlin.uuid.Uuid

internal class CommonClientsDataSource(
    private val httpClient: HttpClient,
    private val clientsDao: ClientsDao,
    preferenceProvider: PreferenceProvider
) : DataSource(), ClientsDataSource, LogOutAction {

    private val preferences = preferenceProvider.get("clients_prefs")

    override suspend fun getClients(businessId: Uuid): List<Client> {
        return mapExceptions {
            httpClient.get(Api.Clients(businessId = businessId))
                .body<List<ClientRemote>>()
                .map { it.toDomain(businessId) }
        }
    }

    override suspend fun getClientsFromDb(businessId: Uuid): List<Client> {
        return mapExceptions {
            clientsDao.getClients(businessId)
                .map { it.toDomain() }
        }
    }

    override suspend fun getClient(id: Uuid): Client {
        return mapExceptions {
            clientsDao.getById(id).toDomain()
        }
    }

    override suspend fun createClient(client: Client): Client {
        return mapExceptions {
            httpClient.post(Api.Clients(businessId = client.businessId)) {
                setBody(client.toRemote())
            }
                .body<ClientRemote>()
                .toDomain(client.businessId)
        }
    }

    override suspend fun updateClient(client: Client): Client {
        return mapExceptions {
            httpClient.patch(Api.Clients.Id(Api.Clients(businessId = client.businessId), id = client.id)) {
                setBody(client.toUpdateRemote())
            }
                .body<ClientRemote>()
                .toDomain(client.businessId)
        }
    }

    override suspend fun deleteClient(businessId: Uuid, id: Uuid) {
        mapExceptions {
            httpClient.delete(Api.Clients.Id(Api.Clients(businessId = businessId), id = id))
        }
    }

    override suspend fun saveClientsInDb(clients: List<Client>) {
        mapExceptions { clientsDao.upsert(clients.map(Client::toDbEntity)) }
    }

    override suspend fun deleteClientInDb(id: Uuid) {
        mapExceptions { clientsDao.deleteById(id) }
    }

    override suspend fun deleteClientsInDb() {
        mapExceptions { clientsDao.clear() }
    }

    override suspend fun getLastSyncedAt(businessId: Uuid): Instant? {
        return preferences.get(Key.lastSyncedAt(businessId))?.let { Instant.fromEpochMilliseconds(it) }
    }

    override suspend fun saveLastSyncedAt(businessId: Uuid) {
        preferences.set(Key.lastSyncedAt(businessId), Clock.System.now().toEpochMilliseconds())
    }

    override suspend fun doOnLogOut() {
        preferences.clear()
        clientsDao.clear()
    }

    private object Key {
        fun lastSyncedAt(businessId: Uuid) = Preferences.Key<Long>("last_synced_at_$businessId")
    }
}