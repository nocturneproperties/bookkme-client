package me.bookk.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow
import me.bookk.database.entity.EmployeeInvitationEntity
import kotlin.uuid.Uuid

@Dao
abstract class EmployeeInvitationDao {
    @Query("select * from employee_invitation where businessId = :businessId")
    abstract fun observeInvitations(businessId: Uuid): Flow<List<EmployeeInvitationEntity>>

    @Query("select id from employee_invitation where businessId = :businessId")
    abstract suspend fun getIdsForBusiness(businessId: Uuid): List<Uuid>

    @Query("delete from employee_invitation where id in (:ids)")
    abstract suspend fun deleteByIds(ids: List<Uuid>)

    @Upsert
    abstract suspend fun upsert(invitations: List<EmployeeInvitationEntity>)

    @Query("delete from employee_invitation")
    abstract suspend fun clear()
}
