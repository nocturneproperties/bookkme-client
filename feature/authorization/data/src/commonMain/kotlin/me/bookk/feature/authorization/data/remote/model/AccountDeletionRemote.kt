package me.bookk.feature.authorization.data.remote.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import kotlin.time.Instant

@Serializable
internal class AccountDeletionRemote(
    @ProtoNumber(1) val deletesAt: Instant,
)

@Serializable
internal class AccountDeletionStatusRemote(
    @ProtoNumber(1) val deletesAt: Instant? = null,
)
