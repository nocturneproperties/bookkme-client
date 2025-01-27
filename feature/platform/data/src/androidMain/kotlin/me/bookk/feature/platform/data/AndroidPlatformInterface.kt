package me.bookk.feature.platform.data

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.net.Uri
import android.os.Build
import me.bookk.feature.platform.domain.datasource.PlatformInterface

class AndroidPlatformInterface(
    private val appContext: Context
) : PlatformInterface {
    override fun getPlatformName(): String {
        return "android"
    }

    override fun getDeviceName(): String {
        if (Build.MODEL.startsWith(Build.MANUFACTURER)) return Build.MODEL
        return buildString {
            append(Build.MANUFACTURER)
            append(" ")
            append(Build.MODEL)
        }
    }

    override fun openUrlPreview(url: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            .addFlags(FLAG_ACTIVITY_NEW_TASK)
        appContext.startActivity(browserIntent)
    }
}