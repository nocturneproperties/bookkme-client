package me.bookk.database

import me.bookk.database.dao.AppointmentDao
import me.bookk.database.dao.AppointmentRequestDao
import me.bookk.database.dao.AppointmentSettingsDao
import me.bookk.database.dao.BookableEmployeeDao
import me.bookk.database.dao.BookingInfoDao
import me.bookk.database.dao.BusinessDao
import me.bookk.database.dao.BusinessDataExportDao
import me.bookk.database.dao.LegalDocumentStatusDao
import me.bookk.database.dao.NotificationSettingsDao
import me.bookk.database.dao.PersonalDataExportDao
import me.bookk.database.dao.SavedBusinessDao
import me.bookk.database.dao.ServiceDao
import me.bookk.database.dao.ServiceGroupDao
import me.bookk.database.dao.UserProfileDao

interface ClientDaos {
    fun profileDao(): UserProfileDao
    fun notificationSettingsDao(): NotificationSettingsDao
    fun legalDocumentStatusDao(): LegalDocumentStatusDao
    fun personalDataExportDao(): PersonalDataExportDao
    fun businessDao(): BusinessDao
    fun businessDataExportDao(): BusinessDataExportDao
    fun savedBusinessDao(): SavedBusinessDao
    fun serviceDao(): ServiceDao
    fun serviceGroupDao(): ServiceGroupDao
    fun appointmentDao(): AppointmentDao
    fun appointmentRequestDao(): AppointmentRequestDao
    fun appointmentSettingsDao(): AppointmentSettingsDao
    fun bookableEmployeeDao(): BookableEmployeeDao
    fun bookingInfoDao(): BookingInfoDao
}
