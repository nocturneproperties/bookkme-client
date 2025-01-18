package me.bookk.feature.platform.domain.api

import me.bookk.feature.platform.domain.entity.PlatformInformation

interface GetPlatformInformation {
    operator fun invoke(): PlatformInformation
}