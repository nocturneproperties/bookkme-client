package me.bookk.database.entity

import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.ForeignKey.Companion.CASCADE
import androidx.room3.Index
import androidx.room3.PrimaryKey
import kotlin.uuid.Uuid

@Entity(
    tableName = "dashboard_business",
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
class DashboardBusinessEntity(
    val businessId: Uuid,
    @PrimaryKey
    val slot: Int = SINGLE_SLOT,
) {
    companion object {
        const val SINGLE_SLOT = 0
    }
}
