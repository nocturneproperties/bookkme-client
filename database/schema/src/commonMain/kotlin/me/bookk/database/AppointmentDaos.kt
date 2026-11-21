package me.bookk.database

import me.bookk.database.dao.AppointmentDao
import me.bookk.database.dao.AppointmentRequestDao
import me.bookk.database.dao.AppointmentSettingsDao

interface AppointmentDaos {
    fun appointmentDao(): AppointmentDao
    fun appointmentRequestDao(): AppointmentRequestDao
    fun appointmentSettingsDao(): AppointmentSettingsDao
}
