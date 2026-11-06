package me.bookk.feature.clients.domain.api.entity

data class ClientsPermissions(
    val canEdit: Boolean,
    val canDeleteDetached: Boolean,
    val canDeleteIntegrated: Boolean,
)
