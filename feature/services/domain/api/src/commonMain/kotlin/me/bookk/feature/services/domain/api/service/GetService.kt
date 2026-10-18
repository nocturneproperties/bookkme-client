package me.bookk.feature.services.domain.api.service

import me.bookk.feature.services.domain.api.service.entity.Service
import kotlin.uuid.Uuid

interface GetService {
    suspend operator fun invoke(id: Uuid): Service

    sealed interface Error {
        class NotFound : Exception()
    }
}
