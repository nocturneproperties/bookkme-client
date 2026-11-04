package me.bookk.feature.authorization.data.remote.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@Serializable
internal class DeleteAccountRequestRemote(@ProtoNumber(1) val confirmationToken: String)
