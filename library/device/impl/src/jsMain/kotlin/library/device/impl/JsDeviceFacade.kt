package library.device.impl

import kotlinx.browser.window
import library.device.api.DeviceFacade

class JsDeviceFacade : DeviceFacade {

    override fun getPlatformName(): String {
        return "web"
    }

    override fun getLocaleId(): String {
        return window.navigator.language
    }

    override fun getCurrencyCode(): String? {
        return null
    }

    override fun getDeviceName(): String {
        return BrowserDeviceName.from(window.navigator.userAgent)
    }

    override fun openUrlPreview(url: String) {
        window.open(url, "_blank", "noopener")
    }

    override fun openMapAt(lat: Double, lng: Double) {
        openUrlPreview("https://www.google.com/maps/search/?api=1&query=$lat,$lng")
    }

    override fun dial(number: String) {
        window.location.assign("tel:$number")
    }

    override fun mail(email: String) {
        window.location.assign("mailto:$email")
    }

    override fun copyToClipboard(text: String) {
        window.navigator.clipboard.writeText(text)
    }

    override fun openAppSettings() {
        return
    }
}
