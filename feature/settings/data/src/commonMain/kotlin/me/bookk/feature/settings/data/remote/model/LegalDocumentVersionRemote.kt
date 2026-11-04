package me.bookk.feature.settings.data.remote.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import me.bookk.feature.settings.domain.api.entity.LegalDocument
import me.bookk.feature.settings.domain.api.entity.LegalDocumentVersion
import kotlin.time.Instant

@Serializable
internal enum class LegalDocumentRemote {
    PRIVACY_POLICY,
    TERMS_OF_SERVICE,
    ;

    fun toDomain(): LegalDocument {
        return when (this) {
            PRIVACY_POLICY -> LegalDocument.PRIVACY_POLICY
            TERMS_OF_SERVICE -> LegalDocument.TERMS_OF_SERVICE
        }
    }
}

@Serializable
internal class LegalDocumentVersionRemote(
    @ProtoNumber(1) val document: LegalDocumentRemote,
    @ProtoNumber(2) val version: String,
    @ProtoNumber(3) val url: String,
    @ProtoNumber(4) val effectiveAt: Instant,
) {
    fun toDomain(): LegalDocumentVersion {
        return LegalDocumentVersion(document.toDomain(), version, url, effectiveAt)
    }
}
