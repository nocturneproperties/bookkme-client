package me.bookk.feature.authorization.data.remote.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@Serializable
class DeleteAccountRemote(
    @ProtoNumber(1) val requestId: String,
    @ProtoNumber(2) val publicKeyCredentialJson: String,
)
