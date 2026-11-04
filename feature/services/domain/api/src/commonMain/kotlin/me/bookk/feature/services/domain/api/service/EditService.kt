package me.bookk.feature.services.domain.api.service

import me.bookk.feature.services.domain.api.service.entity.Service

interface EditService {
    suspend operator fun invoke(service: Service): Service

    sealed interface Error {
        class NameAlreadyExists :
            Exception(),
            Error
        class InvalidName :
            Exception(),
            Error
        class GroupNotFound :
            Exception(),
            Error
        class InvalidPricePrecision :
            Exception(),
            Error
        class InvalidBusinessCurrency :
            Exception(),
            Error
    }
}
