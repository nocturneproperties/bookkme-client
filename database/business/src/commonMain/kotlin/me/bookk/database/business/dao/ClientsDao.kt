package me.bookk.database.business.dao

import androidx.room3.Dao
import androidx.room3.Delete
import androidx.room3.Query
import androidx.room3.Transaction
import androidx.room3.Update
import androidx.room3.Upsert
import kotlinx.coroutines.flow.Flow
import me.bookk.database.business.entity.ClientEntity
import kotlin.uuid.Uuid

@Dao
abstract class ClientsDao {
    @Query("select * from client where businessId = :businessId")
    abstract fun observeClients(businessId: Uuid): Flow<List<ClientEntity>>

    @Query("select * from client where id = :id")
    abstract suspend fun getById(id: Uuid): ClientEntity

    @Query("select * from client where id = :id")
    abstract fun observeById(id: Uuid): Flow<ClientEntity?>

    @Query("select id from client where businessId = :businessId")
    abstract suspend fun getIds(businessId: Uuid): List<Uuid>

    @Upsert
    abstract suspend fun upsert(client: ClientEntity)

    @Upsert
    abstract suspend fun upsert(clients: List<ClientEntity>)

    @Update
    abstract suspend fun update(client: ClientEntity)

    @Delete
    abstract suspend fun delete(client: ClientEntity)

    @Query("DELETE FROM appointment WHERE clientId = :clientId")
    abstract suspend fun deleteCachedAppointments(clientId: Uuid)

    @Query("DELETE FROM appointment_request WHERE clientId = :clientId")
    abstract suspend fun deleteCachedRequests(clientId: Uuid)

    @Transaction
    open suspend fun deleteWithAppointmentCaches(clientId: Uuid) {
        deleteCachedAppointments(clientId)
        deleteCachedRequests(clientId)
        deleteById(clientId)
    }

    @Query("delete from client where id = :clientId")
    abstract suspend fun deleteById(clientId: Uuid)

    @Query("delete from client where id in (:ids)")
    abstract suspend fun deleteByIds(ids: List<Uuid>)

    @Query("delete from client")
    abstract suspend fun clear()
}
