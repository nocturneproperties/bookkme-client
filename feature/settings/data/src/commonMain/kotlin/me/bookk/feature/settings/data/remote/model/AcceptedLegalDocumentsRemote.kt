package me.bookk.feature.settings.data.remote.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@Serializable
internal class AcceptedLegalDocumentsRemote(
    @ProtoNumber(1) val privacyPolicy: String? = null,
    @ProtoNumber(2) val termsOfService: String? = null
)
