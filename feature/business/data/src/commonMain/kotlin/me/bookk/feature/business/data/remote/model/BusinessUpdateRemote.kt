package me.bookk.feature.business.data.remote.model

import kotlinx.datetime.TimeZone
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import me.bookk.core.data.TimeZoneSerializer
import kotlin.uuid.Uuid

@Serializable
internal class BusinessUpdateRemote(
    @ProtoNumber(1) val id: Uuid,
    @ProtoNumber(2) val name: String,
    @ProtoNumber(3) val description: String,
    @ProtoNumber(4) val address: String,
    @ProtoNumber(5) val location: BusinessRemote.Location?,
    @ProtoNumber(6) val currencyCode: String,
    @ProtoNumber(7)
    @Serializable(with = TimeZoneSerializer::class)
    val timeZone: TimeZone,
    @ProtoNumber(8) val socials: List<BusinessRemote.Social>,
    @ProtoNumber(9) val schedule: ScheduleRemote
)
