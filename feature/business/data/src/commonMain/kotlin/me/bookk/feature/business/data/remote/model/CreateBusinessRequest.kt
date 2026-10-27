package me.bookk.feature.business.data.remote.model

import kotlinx.datetime.TimeZone
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import me.bookk.core.data.TimeZoneSerializer

@Serializable
internal class CreateBusinessRequest(
    @ProtoNumber(1) val name: String,
    @ProtoNumber(2) val currencyCode: String,
    @ProtoNumber(3)
    @Serializable(with = TimeZoneSerializer::class)
    val timeZone: TimeZone
)
