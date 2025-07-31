package library.device.impl

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Build
import androidx.core.net.toUri
import library.device.api.DeviceFacade

class AndroidDeviceFacade(
    private val appContext: Context
) : DeviceFacade {
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
        val browserIntent = Intent(Intent.ACTION_VIEW, url.toUri())
            .addFlags(FLAG_ACTIVITY_NEW_TASK)
        runCatching { appContext.startActivity(browserIntent) }
    }

    override fun openMapAt(lat: Double, lng: Double) {
        val geoUrl = "geo:$lat,$lng?z=15"
        openUrlPreview(geoUrl)
    }
}