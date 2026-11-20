package me.bookk.feature.authorization.data.remote.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@Serializable
class TelegramSignInRequestRemote(
    @ProtoNumber(1) val initData: String,
    @ProtoNumber(2) val deviceInfo: VerifyAuthRequest.DeviceInfo,
    @ProtoNumber(3) val acceptedDocuments: AcceptedLegalDocumentsRemote? = null,
)
