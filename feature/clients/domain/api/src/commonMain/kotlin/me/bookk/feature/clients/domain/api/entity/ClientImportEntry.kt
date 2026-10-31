package me.bookk.feature.clients.domain.api.entity

data class ClientImportEntry(
    val name: String,
    val lastName: String,
    val phone: String?,
    val email: String?,
    val description: String? = null
)
