package me.bookk.feature.settings.data.remote.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import me.bookk.feature.settings.domain.api.entity.LegalDocumentStatus
import kotlin.time.Instant

@Serializable
internal class LegalDocumentStatusRemote(
    @ProtoNumber(1) val current: LegalDocumentVersionRemote,
    @ProtoNumber(2) val acceptedAt: Instant? = null
) {
    fun toDomain(): LegalDocumentStatus {
        return LegalDocumentStatus(current.toDomain(), acceptedAt)
    }
}
