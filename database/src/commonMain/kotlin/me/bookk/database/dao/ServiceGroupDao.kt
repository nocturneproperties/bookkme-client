package me.bookk.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import me.bookk.database.entity.ServiceGroupEntity
import kotlin.uuid.Uuid

@Dao
abstract class ServiceGroupDao {
    @Query("select * from service_group where businessId = :businessId")
    abstract fun observe(businessId: Uuid): Flow<List<ServiceGroupEntity>>

    @Query("select * from service_group where id = :id")
    abstract suspend fun getById(id: Uuid): ServiceGroupEntity

    @Query("select id from service_group where businessId = :businessId")
    abstract suspend fun getIds(businessId: Uuid): List<Uuid>

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

    @Query("delete from service_group where id in (:ids)")
    abstract suspend fun deleteByIds(ids: List<Uuid>)

    @Query("delete from service_group")
    abstract suspend fun clear()
}