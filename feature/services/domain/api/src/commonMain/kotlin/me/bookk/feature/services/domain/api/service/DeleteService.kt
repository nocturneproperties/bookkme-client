package me.bookk.feature.services.domain.api.service

import me.bookk.feature.services.domain.api.service.entity.Service

interface DeleteService {
    suspend operator fun invoke(service: Service)
}