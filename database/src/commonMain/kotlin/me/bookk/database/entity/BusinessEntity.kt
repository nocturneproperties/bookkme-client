package me.bookk.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.uuid.Uuid

@Entity(
    tableName = "business"
)
class BusinessEntity(
    @PrimaryKey
    val id: Uuid,
    val name: String,
    val description: String,
    val address: String,
    val locationLat: Double?,
    val locationLng: Double?,
    val currencyCode: String,
    val phone: String?,
    val insta: String?,
    val viber: String?,
    val whatsApp: String?,
    val telegram: String?,
    val timeZone: String,
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
    @ColumnInfo(defaultValue = "0") val appointmentsPermissionDelete: Boolean = false
)
