package me.bookk.feature.business.data.remote.model

import kotlinx.serialization.Serializable

@Serializable
internal class CreateBusinessRequest(
    val name: String
)