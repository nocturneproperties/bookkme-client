package me.bookk.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import me.bookk.database.entity.ServiceGroupEntity
import kotlin.uuid.Uuid

@Dao
abstract class ServiceGroupDao {
    @Query("select * from service_group where businessId = :businessId")
    abstract suspend fun get(businessId: Uuid): List<ServiceGroupEntity>

    @Query("select * from service_group where id = :id")
    abstract suspend fun getById(id: Uuid): ServiceGroupEntity

    @Upsert
    abstract suspend fun upsert(client: ServiceGroupEntity)

    @Upsert
    abstract suspend fun upsert(clients: List<ServiceGroupEntity>)

    @Update
    abstract suspend fun update(client: ServiceGroupEntity)

    @Delete
    abstract suspend fun delete(client: ServiceGroupEntity)

    @Query("delete from service_group where id = :clientId")
    abstract suspend fun deleteById(clientId: Uuid)

    @Query("delete from service_group")
    abstract suspend fun clear()
}