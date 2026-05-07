package me.bookk.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import me.bookk.database.entity.ClientEntity
import kotlin.uuid.Uuid

@Dao
abstract class ClientsDao {
    @Query("select * from client where businessId = :businessId")
    abstract suspend fun getClients(businessId: Uuid): List<ClientEntity>

    @Query("select * from client where id = :id")
    abstract suspend fun getById(id: Uuid): ClientEntity

    @Upsert
    abstract suspend fun upsert(client: ClientEntity)

    @Upsert
    abstract suspend fun upsert(clients: List<ClientEntity>)

    @Update
    abstract suspend fun update(client: ClientEntity)

    @Delete
    abstract suspend fun delete(client: ClientEntity)

    @Query("delete from client where id = :clientId")
    abstract suspend fun deleteById(clientId: Uuid)

    @Query("delete from client")
    abstract suspend fun clear()
}