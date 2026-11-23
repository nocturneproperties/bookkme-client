package me.bookk.feature.business.domain.api.link

import me.bookk.feature.business.domain.api.entity.BusinessLink

interface ResolveBusinessLink {
    suspend operator fun invoke(name: String): BusinessLink

    sealed interface Error {
        class NotFound :
            Exception(),
            Error
    }
}
