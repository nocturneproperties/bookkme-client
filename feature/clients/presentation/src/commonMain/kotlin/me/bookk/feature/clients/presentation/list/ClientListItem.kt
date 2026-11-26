package me.bookk.feature.clients.presentation.list

data class ClientListItem(
    val id: String,
    val fullName: String,
    val onClick: () -> Unit,
)
