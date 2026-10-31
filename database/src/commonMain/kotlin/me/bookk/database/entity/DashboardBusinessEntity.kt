package me.bookk.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlin.uuid.Uuid

@Entity(
    tableName = "dashboard_business",
    foreignKeys = [
        ForeignKey(
            entity = BusinessEntity::class,
            parentColumns = ["id"],
            childColumns = ["businessId"],
            onDelete = CASCADE
        )
    ],
    indices = [Index("businessId")]
)
class DashboardBusinessEntity(
    val businessId: Uuid,
    @PrimaryKey
    val slot: Int = SINGLE_SLOT
) {
    companion object {
        const val SINGLE_SLOT = 0
    }
}
