package me.bookk.feature.business.data.remote.model

import kotlinx.datetime.TimeZone
import kotlinx.serialization.Serializable

@Serializable
internal class CreateBusinessRequest(
    val name: String,
    val currencyCode: String,
    val timeZone: TimeZone
)