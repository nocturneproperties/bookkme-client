package me.bookk.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class TempEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long
)