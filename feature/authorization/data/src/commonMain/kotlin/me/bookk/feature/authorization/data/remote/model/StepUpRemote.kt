package me.bookk.feature.authorization.data.remote.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import me.bookk.feature.authorization.domain.entity.SensitiveAction
import kotlin.uuid.Uuid

@Serializable
internal enum class SensitiveActionRemote {
    DELETE_CLIENT,
    EXPORT_PERSONAL_DATA,
    EXPORT_BUSINESS_DATA;

    companion object {
        fun fromDomain(action: SensitiveAction): SensitiveActionRemote {
            return when (action) {
                SensitiveAction.DELETE_CLIENT -> DELETE_CLIENT
                SensitiveAction.EXPORT_PERSONAL_DATA -> EXPORT_PERSONAL_DATA
                SensitiveAction.EXPORT_BUSINESS_DATA -> EXPORT_BUSINESS_DATA
            }
        }
    }
}

@Serializable
internal class StepUpRequestRemote(
    @ProtoNumber(1) val requestId: String,
    @ProtoNumber(2) val publicKeyCredentialJson: String,
    @ProtoNumber(3) val action: SensitiveActionRemote,
    @ProtoNumber(4) val businessId: Uuid,
    @ProtoNumber(5) val targetId: Uuid
)

@Serializable
internal class StepUpTokenRemote(@ProtoNumber(1) val token: String)
