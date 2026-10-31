package me.bookk.feature.clients.domain.api

import me.bookk.feature.clients.domain.api.entity.ClientImportEntry

interface GetDeviceContacts {
    suspend operator fun invoke(): List<ClientImportEntry>
}
