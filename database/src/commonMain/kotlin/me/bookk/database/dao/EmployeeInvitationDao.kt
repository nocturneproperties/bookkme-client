package me.bookk.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import me.bookk.database.entity.EmployeeInvitationEntity
import kotlin.uuid.Uuid

@Dao
abstract class EmployeeInvitationDao {
    @Query("select * from employee_invitation where businessId = :businessId")
    abstract suspend fun getInvitations(businessId: Uuid): List<EmployeeInvitationEntity>

    @Upsert
    abstract suspend fun upsert(invitations: List<EmployeeInvitationEntity>)

    @Query("delete from employee_invitation")
    abstract suspend fun clear()
}
