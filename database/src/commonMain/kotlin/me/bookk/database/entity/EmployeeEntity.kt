package me.bookk.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlin.time.Instant
import kotlin.uuid.Uuid

@Entity(
    tableName = "employee",
    foreignKeys = [
        ForeignKey(
            entity = BusinessEntity::class,
            parentColumns = ["id"],
            childColumns = ["businessId"],
            onDelete = CASCADE
        ),
    ],
    indices = [Index("businessId")]
)
class EmployeeEntity(
    @PrimaryKey
    val id: Uuid,
    val businessId: Uuid,
    val name: String,
    val lastName: String,
    val phone: String?,
    val email: String?,
    val userId: Uuid,
    val createdAt: Instant,
    @ColumnInfo(defaultValue = "0") val businessPermissionView: Boolean = false,
    @ColumnInfo(defaultValue = "0") val businessPermissionUpdate: Boolean = false,
    @ColumnInfo(defaultValue = "0") val businessPermissionDelete: Boolean = false,
    @ColumnInfo(defaultValue = "0") val employeesPermissionView: Boolean = false,
    @ColumnInfo(defaultValue = "0") val employeesPermissionUpdate: Boolean = false,
    @ColumnInfo(defaultValue = "0") val employeesPermissionDelete: Boolean = false,
    @ColumnInfo(defaultValue = "0") val clientsPermissionView: Boolean = false,
    @ColumnInfo(defaultValue = "0") val clientsPermissionUpdate: Boolean = false,
    @ColumnInfo(defaultValue = "0") val clientsPermissionDelete: Boolean = false,
    @ColumnInfo(defaultValue = "0") val servicesPermissionView: Boolean = false,
    @ColumnInfo(defaultValue = "0") val servicesPermissionUpdate: Boolean = false,
    @ColumnInfo(defaultValue = "0") val servicesPermissionDelete: Boolean = false,
    @ColumnInfo(defaultValue = "0") val appointmentsPermissionView: Boolean = false,
    @ColumnInfo(defaultValue = "0") val appointmentsPermissionUpdate: Boolean = false,
    @ColumnInfo(defaultValue = "0") val appointmentsPermissionDelete: Boolean = false,
    val suspendedAt: Instant? = null
)
