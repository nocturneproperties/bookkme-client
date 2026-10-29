package me.bookk.database.relation

import androidx.room.Embedded

class DashboardFlaggedBusinessLocal(
    @Embedded val business: BusinessLocal,
    val isDashboard: Boolean
)
