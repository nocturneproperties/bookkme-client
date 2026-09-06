package me.bookk.feature.clients.data.datasource

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.resources.delete
import io.ktor.client.plugins.resources.get
import io.ktor.client.plugins.resources.patch
import io.ktor.client.plugins.resources.post
import io.ktor.client.request.setBody
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
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
import kotlin.uuid.Uuid

internal class CommonClientsDataSource(
    private val httpClient: HttpClient,
    private val clientsDao: ClientsDao
) : DataSource(), ClientsDataSource, LogOutAction {

    override suspend fun getClients(businessId: Uuid): List<Client> {
        return mapExceptions {
            httpClient.get(Api.Clients(businessId = businessId))
                .body<List<ClientRemote>>()
                .map { it.toDomain(businessId) }
        }
    }

    override fun observeClientsDBChanges(businessId: Uuid): Flow<List<Client>> {
        return clientsDao.observeClients(businessId)
            .map { clients -> clients.map { it.toDomain() } }
            .mapErrors()
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

    override suspend fun deleteClientsInDb(businessId: Uuid) {
        mapExceptions { clientsDao.deleteByBusinessId(businessId) }
    }

    override suspend fun doOnLogOut() {
        clientsDao.clear()
    }
}
