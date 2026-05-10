package me.bookk.feature.services.domain.api.service

import me.bookk.feature.services.domain.api.service.entity.Service

interface EditService {
    suspend operator fun invoke(service: Service): Service
}