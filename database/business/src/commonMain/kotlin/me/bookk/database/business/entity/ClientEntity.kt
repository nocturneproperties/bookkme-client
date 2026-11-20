package me.bookk.database.business.entity

import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.ForeignKey.Companion.CASCADE
import androidx.room3.Index
import androidx.room3.PrimaryKey
import kotlin.uuid.Uuid

@Entity(
    tableName = "client",
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
class ClientEntity(
    @PrimaryKey
    val id: Uuid,
    val name: String,
    val lastName: String,
    val phone: String?,
    val email: String?,
    val businessId: Uuid,
    val userId: Uuid?,
    val description: String? = null,
)
