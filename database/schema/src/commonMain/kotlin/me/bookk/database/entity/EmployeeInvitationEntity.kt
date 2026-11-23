package me.bookk.database.entity

import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.ForeignKey.Companion.CASCADE
import androidx.room3.Index
import androidx.room3.PrimaryKey
import kotlin.time.Instant
import kotlin.uuid.Uuid

@Entity(
    tableName = "employee_invitation",
    foreignKeys = [
        ForeignKey(
            entity = BusinessEntity::class,
            parentColumns = ["id"],
            childColumns = ["businessId"],
            onDelete = CASCADE,
        ),
    ],
    indices = [Index("businessId")],
)
class EmployeeInvitationEntity(
    @PrimaryKey
    val id: Uuid,
    val businessId: Uuid,
    val invitedBy: Uuid,
    val code: String?,
    val status: String,
    val createdAt: Instant,
)
