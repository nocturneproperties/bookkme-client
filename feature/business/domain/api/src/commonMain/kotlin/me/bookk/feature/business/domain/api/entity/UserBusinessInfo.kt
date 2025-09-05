package me.bookk.feature.business.domain.api.entity

import kotlin.uuid.Uuid

class UserBusinessInfo(
    val dashboardId: Uuid,
    val businesses: List<Business>
)