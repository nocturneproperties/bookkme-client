package me.bookk.feature.business.data.remote.model

import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
class UserBusinessesRemote(
    val dashboardId: Uuid,
    val businesses: List<BusinessRemote>
)