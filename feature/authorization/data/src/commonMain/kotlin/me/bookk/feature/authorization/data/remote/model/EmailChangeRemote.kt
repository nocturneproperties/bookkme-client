package me.bookk.feature.authorization.data.remote.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import kotlin.uuid.Uuid

@Serializable
class EmailChangeRequestRemote(
    @ProtoNumber(1) val email: String,
)

@Serializable
class EmailConfirmationRemote(
    @ProtoNumber(1) val verificationId: Uuid,
    @ProtoNumber(2) val code: String,
)
