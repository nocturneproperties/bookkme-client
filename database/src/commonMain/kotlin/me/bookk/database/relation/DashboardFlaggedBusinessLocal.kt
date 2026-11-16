package me.bookk.database.relation

import androidx.room3.Embedded

class DashboardFlaggedBusinessLocal(
    @Embedded val business: BusinessLocal,
    val isDashboard: Boolean,
)
