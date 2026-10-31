package me.bookk.feature.clients.domain.api

import me.bookk.feature.clients.domain.api.entity.ClientImportEntry
import me.bookk.feature.clients.domain.api.entity.ClientImportResult
import kotlin.uuid.Uuid

interface ImportClients {
    suspend operator fun invoke(businessId: Uuid, entries: List<ClientImportEntry>): ClientImportResult
}
