package me.bookk.feature.platform.domain.datasource

interface PlatformInterface {
    fun getPlatformName(): String
    fun getDeviceName(): String
    fun openUrlPreview(url: String)
}