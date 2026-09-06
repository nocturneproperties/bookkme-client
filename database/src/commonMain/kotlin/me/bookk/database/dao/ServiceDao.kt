package me.bookk.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import me.bookk.database.entity.ServiceEntity
import me.bookk.database.relation.ServiceLocal
import kotlin.uuid.Uuid

@Dao
abstract class ServiceDao {
    @Query("select * from service where businessId = :businessId")
    @Transaction
    abstract fun observe(businessId: Uuid): Flow<List<ServiceLocal>>

    @Query("select id from service where businessId = :businessId")
    abstract suspend fun getIds(businessId: Uuid): List<Uuid>

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

    @Query("delete from service where id in (:ids)")
    abstract suspend fun deleteByIds(ids: List<Uuid>)

    @Query("delete from service")
    abstract suspend fun clear()
}