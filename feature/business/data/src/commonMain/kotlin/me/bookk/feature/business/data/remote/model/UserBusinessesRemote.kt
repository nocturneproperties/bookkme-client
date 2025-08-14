package me.bookk.feature.business.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
class UserBusinessesRemote(
    val dashboardId: Long,
    val businesses: List<BusinessRemote>
)