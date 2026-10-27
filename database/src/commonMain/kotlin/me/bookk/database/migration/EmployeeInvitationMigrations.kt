package me.bookk.database.migration

import androidx.room.DeleteColumn
import androidx.room.migration.AutoMigrationSpec

@DeleteColumn(tableName = "employee_invitation", columnName = "email")
class DeleteEmployeeInvitationEmail : AutoMigrationSpec
