package me.bookk.feature.clients.presentation.list

import me.bookk.feature.clients.domain.api.entity.Client

data class ClientSection(
    val id: String,
    val header: String,
    val items: List<Client>,
    val onItemClick: (Client) -> Unit
)