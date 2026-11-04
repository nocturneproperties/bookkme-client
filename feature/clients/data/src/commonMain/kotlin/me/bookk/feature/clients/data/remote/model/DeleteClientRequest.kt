package me.bookk.feature.clients.data.remote.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@Serializable
internal class DeleteClientRequest(@ProtoNumber(1) val stepUpToken: String)
