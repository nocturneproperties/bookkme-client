package me.bookk.core

import platform.UIKit.UIDevice

actual class Platform actual constructor() {
    actual fun getPlatformName(): String = "ios"

    actual fun getDeviceName(): String {
        return UIDevice.currentDevice.name
    }
}