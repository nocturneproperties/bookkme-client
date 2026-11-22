package me.bookk.database

import me.bookk.database.dao.BusinessDao
import me.bookk.database.dao.BusinessDataExportDao
import me.bookk.database.dao.SavedBusinessDao

interface ClientDaos {
    fun businessDao(): BusinessDao
    fun businessDataExportDao(): BusinessDataExportDao
    fun savedBusinessDao(): SavedBusinessDao
}
