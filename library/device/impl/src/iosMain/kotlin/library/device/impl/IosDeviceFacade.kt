package library.device.impl

import library.device.api.DeviceFacade
import platform.Foundation.NSLocale
import platform.Foundation.NSURL
import platform.Foundation.canonicalLanguageIdentifierFromString
import platform.Foundation.currentLocale
import platform.Foundation.localeIdentifier
import platform.UIKit.UIApplication
import platform.UIKit.UIDevice
import platform.UIKit.UIPasteboard

class IosDeviceFacade : DeviceFacade {

    override fun getPlatformName(): String {
        return "ios"
    }

    override fun getLocaleId(): String {
        return NSLocale.canonicalLanguageIdentifierFromString(NSLocale.currentLocale.localeIdentifier)
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

    override fun dial(number: String) {
        openUrlPreview("tel://$number")
    }

    override fun mail(email: String) {
        openUrlPreview("mailto://$email")
    }

    override fun copyToClipboard(text: String) {
        UIPasteboard.generalPasteboard.string = text
    }
}