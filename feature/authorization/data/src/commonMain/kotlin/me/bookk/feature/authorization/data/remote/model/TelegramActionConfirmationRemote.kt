package me.bookk.feature.authorization.data.remote.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import kotlin.uuid.Uuid

@Serializable
internal class TelegramActionConfirmationStartRemote(
    @ProtoNumber(1) val action: SensitiveActionRemote,
    @ProtoNumber(2) val businessId: Uuid,
    @ProtoNumber(3) val targetId: Uuid,
)

@Serializable
internal class TelegramActionConfirmationRequestRemote(
    @ProtoNumber(1) val initData: String,
)
