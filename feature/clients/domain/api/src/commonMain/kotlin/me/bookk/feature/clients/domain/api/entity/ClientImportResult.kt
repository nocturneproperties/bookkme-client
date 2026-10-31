package me.bookk.feature.clients.domain.api.entity

data class ClientImportResult(
    val created: List<Client>,
    val rejected: List<RejectedClientImport>
)

data class RejectedClientImport(
    val index: Int,
    val reason: Reason
) {
    enum class Reason {
        INVALID_FIELDS,
        MISSING_CONTACT_INFO,
        ALREADY_EXISTS,
        UNKNOWN
    }
}
