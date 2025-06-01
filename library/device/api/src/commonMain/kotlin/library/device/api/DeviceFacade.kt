package library.device.api

interface DeviceFacade {
    fun getPlatformName(): String
    fun getDeviceName(): String
    fun openUrlPreview(url: String)
}