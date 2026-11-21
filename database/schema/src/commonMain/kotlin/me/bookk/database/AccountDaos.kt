package me.bookk.database

import me.bookk.database.dao.LegalDocumentStatusDao
import me.bookk.database.dao.NotificationSettingsDao
import me.bookk.database.dao.PersonalDataExportDao
import me.bookk.database.dao.UserProfileDao

interface AccountDaos {
    fun profileDao(): UserProfileDao
    fun notificationSettingsDao(): NotificationSettingsDao
    fun legalDocumentStatusDao(): LegalDocumentStatusDao
    fun personalDataExportDao(): PersonalDataExportDao
}
