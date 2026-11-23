package me.bookk.database.entity

import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.ForeignKey.Companion.CASCADE
import androidx.room3.Index
import androidx.room3.PrimaryKey
import kotlin.uuid.Uuid

@Entity(
    tableName = "bookable_employee",
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
class BookableEmployeeEntity(
    @PrimaryKey
    val id: Uuid,
    val businessId: Uuid,
    val name: String,
    val lastName: String,
    val position: Int,
)
