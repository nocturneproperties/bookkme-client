package me.bookk.feature.settings.data.remote.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@Serializable
class ContactFormRemote(
    @ProtoNumber(1) val text: String,
    @ProtoNumber(2) val usageLogs: String?
)
