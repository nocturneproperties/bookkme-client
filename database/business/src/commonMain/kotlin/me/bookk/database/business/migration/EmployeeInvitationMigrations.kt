package me.bookk.database.business.migration

import androidx.room3.DeleteColumn
import androidx.room3.migration.AutoMigrationSpec

@DeleteColumn(tableName = "employee_invitation", columnName = "email")
class DeleteEmployeeInvitationEmail : AutoMigrationSpec
