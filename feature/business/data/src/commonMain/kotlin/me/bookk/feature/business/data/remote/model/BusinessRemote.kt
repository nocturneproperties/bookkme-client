package me.bookk.feature.business.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
class BusinessRemote(
    val id: Long,
    val name: String
)