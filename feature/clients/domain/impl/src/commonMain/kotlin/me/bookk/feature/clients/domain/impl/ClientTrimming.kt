package me.bookk.feature.clients.domain.impl

import me.bookk.feature.clients.domain.api.entity.Client

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
