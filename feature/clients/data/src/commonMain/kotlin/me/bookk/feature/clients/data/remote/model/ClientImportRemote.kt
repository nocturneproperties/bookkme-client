package me.bookk.feature.clients.data.remote.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber
import me.bookk.feature.clients.data.mapping.toDomain
import me.bookk.feature.clients.domain.api.entity.ClientImportEntry
import me.bookk.feature.clients.domain.api.entity.ClientImportResult
import me.bookk.feature.clients.domain.api.entity.RejectedClientImport
import me.bookk.feature.clients.domain.datasource.ClientErrorCodes
import kotlin.uuid.Uuid

@Serializable
internal class ClientImportRequest(
    @ProtoNumber(1) val clients: List<ClientImportEntryRemote>
)

@Serializable
internal class ClientImportEntryRemote(
    @ProtoNumber(1) val name: String,
    @ProtoNumber(2) val lastName: String,
    @ProtoNumber(3) val phone: String?,
    @ProtoNumber(4) val email: String?,
    @ProtoNumber(5) val description: String?
) {
    companion object {
        fun from(entry: ClientImportEntry): ClientImportEntryRemote {
            return ClientImportEntryRemote(
                name = entry.name,
                lastName = entry.lastName,
                phone = entry.phone,
                email = entry.email,
                description = entry.description
            )
        }
    }
}

@Serializable
internal class ClientImportResultRemote(
    @ProtoNumber(1) val created: List<ClientRemote>,
    @ProtoNumber(2) val rejected: List<RejectedClientRemote>
) {
    fun toDomain(businessId: Uuid): ClientImportResult {
        return ClientImportResult(
            created = created.map { it.toDomain(businessId) },
            rejected = rejected.map(RejectedClientRemote::toDomain)
        )
    }
}

@Serializable
internal class RejectedClientRemote(
    @ProtoNumber(1) val index: Int,
    @ProtoNumber(2) val errorCode: Int
) {
    fun toDomain(): RejectedClientImport {
        return RejectedClientImport(index = index, reason = reason())
    }

    private fun reason(): RejectedClientImport.Reason {
        return when (errorCode) {
            ClientErrorCodes.CLIENT_VALIDATION_ERROR -> RejectedClientImport.Reason.INVALID_FIELDS
            ClientErrorCodes.CLIENT_MISSING_CONTACT_INFO -> RejectedClientImport.Reason.MISSING_CONTACT_INFO
            ClientErrorCodes.CLIENT_EXISTS -> RejectedClientImport.Reason.ALREADY_EXISTS
            else -> RejectedClientImport.Reason.UNKNOWN
        }
    }
}
