package me.bookk.core

expect class Platform() {
    fun getPlatformName(): String
    fun getDeviceName(): String
}