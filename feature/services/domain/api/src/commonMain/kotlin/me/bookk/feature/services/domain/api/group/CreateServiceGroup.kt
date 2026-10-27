package me.bookk.feature.services.domain.api.group

import me.bookk.feature.services.domain.api.group.entity.ServiceGroup

interface CreateServiceGroup {
    suspend operator fun invoke(group: ServiceGroup): ServiceGroup

    sealed interface Error {
        class NameExists(cause: Throwable) : Error, Throwable(cause)
        class InvalidName(cause: Throwable) : Error, Throwable(cause)
    }
}