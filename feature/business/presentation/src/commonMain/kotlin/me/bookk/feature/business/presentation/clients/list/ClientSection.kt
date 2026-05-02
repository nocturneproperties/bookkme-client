package me.bookk.feature.business.presentation.clients.list

import me.bookk.feature.business.domain.api.entity.Client

data class ClientSection(
    val id: String,
    val header: String,
    val items: List<Client>
)