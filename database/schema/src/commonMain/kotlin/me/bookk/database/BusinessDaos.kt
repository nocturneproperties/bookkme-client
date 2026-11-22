package me.bookk.database

import me.bookk.database.dao.BusinessDao
import me.bookk.database.dao.BusinessDataExportDao
import me.bookk.database.dao.BusinessLinkDao

interface BusinessDaos {
    fun businessDao(): BusinessDao
    fun businessDataExportDao(): BusinessDataExportDao
    fun businessLinkDao(): BusinessLinkDao
}
