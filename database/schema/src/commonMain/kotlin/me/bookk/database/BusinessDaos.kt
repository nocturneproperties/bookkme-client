package me.bookk.database

import me.bookk.database.dao.BusinessDao
import me.bookk.database.dao.BusinessDataExportDao

interface BusinessDaos {
    fun businessDao(): BusinessDao
    fun businessDataExportDao(): BusinessDataExportDao
}
