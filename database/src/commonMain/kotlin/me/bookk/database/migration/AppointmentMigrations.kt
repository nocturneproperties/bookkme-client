package me.bookk.database.migration

import androidx.room.DeleteColumn
import androidx.room.migration.AutoMigrationSpec

@DeleteColumn(tableName = "appointment", columnName = "localDate")
class DeleteAppointmentLocalDateMigration : AutoMigrationSpec
