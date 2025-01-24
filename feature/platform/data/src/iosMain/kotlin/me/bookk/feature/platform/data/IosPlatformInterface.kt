package me.bookk.feature.platform.data

import me.bookk.feature.platform.domain.datasource.PlatformInterface
import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.UIKit.UIDevice

class IosPlatformInterface : PlatformInterface {

    override fun getPlatformName(): String {
        return "ios"
    }

    override fun getDeviceName(): String {
        return UIDevice.currentDevice.name
    }

    override fun openUrlPreview(url: String) {
        NSURL.URLWithString(url)?.let {
            UIApplication.sharedApplication.openURL(
                url = it,
                options = emptyMap<Any?, Any>(),
                completionHandler = null
            )
        }
    }
}