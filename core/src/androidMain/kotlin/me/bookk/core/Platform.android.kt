package me.bookk.core

import android.os.Build

actual class Platform actual constructor() {
    actual fun getPlatformName(): String = "android"

    actual fun getDeviceName(): String {
        if (Build.MODEL.startsWith(Build.MANUFACTURER)) return Build.MODEL
        return buildString {
            append(Build.MANUFACTURER)
            append(" ")
            append(Build.MODEL)
        }
    }
}