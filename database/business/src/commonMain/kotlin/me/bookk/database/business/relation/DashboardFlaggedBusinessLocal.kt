package me.bookk.database.business.relation

import androidx.room3.Embedded

class DashboardFlaggedBusinessLocal(
    @Embedded val business: BusinessLocal,
    val isDashboard: Boolean,
)
