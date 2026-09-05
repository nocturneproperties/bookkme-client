package library.device.api

interface DeviceFacade {
    fun getPlatformName(): String
    fun getLocaleId(): String
    fun getDeviceName(): String
    fun openUrlPreview(url: String)
    fun openMapAt(lat: Double, lng: Double)
    fun dial(number: String)
    fun mail(email: String)
    fun copyToClipboard(text: String)
}