package me.bookk.feature.clients.domain.impl

import me.bookk.feature.clients.domain.api.entity.Client
import me.bookk.feature.clients.domain.api.entity.ClientImportEntry

internal fun Client.trimmed(): Client {
    return when (this) {
        is Client.Detached -> copy(
            name = name.trim(),
            lastName = lastName.trim(),
            phone = phone?.trim(),
            email = email?.trim(),
            description = description?.trim()
        )

        is Client.Integrated -> copy(
            name = name.trim(),
            lastName = lastName.trim(),
            phone = phone?.trim(),
            email = email?.trim(),
            description = description?.trim()
        )
    }
}

internal fun ClientImportEntry.trimmed(): ClientImportEntry {
    return copy(
        name = name.trim(),
        lastName = lastName.trim(),
        phone = phone?.trim(),
        email = email?.trim(),
        description = description?.trim()
    )
}
