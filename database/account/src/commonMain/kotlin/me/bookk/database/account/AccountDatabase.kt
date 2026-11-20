package me.bookk.database.account

import me.bookk.database.account.dao.LegalDocumentStatusDao
import me.bookk.database.account.dao.NotificationSettingsDao
import me.bookk.database.account.dao.PersonalDataExportDao
import me.bookk.database.account.dao.UserProfileDao

interface AccountDatabase {
    fun profileDao(): UserProfileDao
    fun notificationSettingsDao(): NotificationSettingsDao
    fun legalDocumentStatusDao(): LegalDocumentStatusDao
    fun personalDataExportDao(): PersonalDataExportDao
}
