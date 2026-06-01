package me.bookk.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import me.bookk.database.entity.ServiceEntity
import me.bookk.database.relation.ServiceLocal
import kotlin.uuid.Uuid

@Dao
abstract class ServiceDao {
    @Query("select * from service where businessId = :businessId")
    @Transaction
    abstract suspend fun get(businessId: Uuid): List<ServiceLocal>

    @Query("select * from service where id = :id")
    @Transaction
    abstract suspend fun getById(id: Uuid): ServiceLocal

    @Upsert
    abstract suspend fun upsert(client: ServiceEntity)

    @Upsert
    abstract suspend fun upsert(clients: List<ServiceEntity>)

    @Update
    abstract suspend fun update(client: ServiceEntity)

    @Delete
    abstract suspend fun delete(client: ServiceEntity)

    @Query("delete from service where id = :clientId")
    abstract suspend fun deleteById(clientId: Uuid)

    @Query("delete from service")
    abstract suspend fun clear()
}