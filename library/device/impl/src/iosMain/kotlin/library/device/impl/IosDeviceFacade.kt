package library.device.impl

import library.device.api.DeviceFacade
import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.UIKit.UIDevice

class IosDeviceFacade : DeviceFacade {

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

    override fun openMapAt(lat: Double, lng: Double) {
        val url = "http://maps.apple.com/?ll=$lat,$lng&z=15"
        openUrlPreview(url)
    }
}