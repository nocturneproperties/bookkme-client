package me.bookk.feature.business.data.remote.model

import kotlinx.datetime.TimeZone
import kotlinx.serialization.Serializable
import me.bookk.core.data.TimeZoneSerializer

@Serializable
internal class CreateBusinessRequest(
    val name: String,
    val currencyCode: String,
    @Serializable(with = TimeZoneSerializer::class)
    val timeZone: TimeZone
)