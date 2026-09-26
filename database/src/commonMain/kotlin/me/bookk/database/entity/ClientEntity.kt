package me.bookk.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlin.uuid.Uuid

@Entity(
    tableName = "client",
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
class ClientEntity(
    @PrimaryKey
    val id: Uuid,
    val name: String,
    val lastName: String,
    val phone: String?,
    val email: String?,
    val businessId: Uuid,
    val userId: Uuid?,
    val description: String? = null
)