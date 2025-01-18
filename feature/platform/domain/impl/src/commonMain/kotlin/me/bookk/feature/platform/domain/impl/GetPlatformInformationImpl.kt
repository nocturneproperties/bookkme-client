package me.bookk.feature.platform.domain.impl

import me.bookk.feature.platform.domain.api.GetPlatformInformation
import me.bookk.feature.platform.domain.datasource.PlatformInterface
import me.bookk.feature.platform.domain.entity.PlatformInformation

class GetPlatformInformationImpl(
    private val platformInterface: PlatformInterface
) : GetPlatformInformation {
    override fun invoke(): PlatformInformation {
        return PlatformInformation(
            platformName = platformInterface.getPlatformName(),
            deviceName = platformInterface.getDeviceName()
        )
    }
}