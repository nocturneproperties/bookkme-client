package me.bookk.feature.business.domain.api.link

import me.bookk.feature.business.domain.api.entity.BusinessLink
import kotlin.uuid.Uuid

interface SetBusinessLink {
    suspend operator fun invoke(businessId: Uuid, name: String): BusinessLink

    sealed interface Error {
        class Invalid :
            Exception(),
            Error
        class Reserved :
            Exception(),
            Error
        class Taken :
            Exception(),
            Error
        class ChangeLimitReached :
            Exception(),
            Error
        class NotOwner :
            Exception(),
            Error
    }
}
