package me.bookk.feature.business.data.remote.model

import kotlinx.datetime.TimeZone
import kotlinx.serialization.Serializable
import me.bookk.core.data.TimeZoneSerializer
import kotlin.uuid.Uuid

@Serializable
internal class BusinessUpdateRemote(
    val id: Uuid,
    val name: String,
    val description: String,
    val address: String,
    val location: BusinessRemote.Location?,
    val currencyCode: String,
    @Serializable(with = TimeZoneSerializer::class)
    val timeZone: TimeZone,
    val socials: List<BusinessRemote.Social>
)
