package library.device.impl

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Build
import androidx.core.net.toUri
import library.device.api.DeviceFacade
import me.bookk.core.Logger


class AndroidDeviceFacade(
    private val appContext: Context
) : DeviceFacade {

    private val logger = Logger.create("DeviceFacade")

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

    override fun dial(number: String) {
        val intent = Intent(Intent.ACTION_DIAL, "tel:$number".toUri())
            .addFlags(FLAG_ACTIVITY_NEW_TASK)
        runCatching { appContext.startActivity(intent) }
            .onFailure { logger.e(it) }
    }

    override fun mail(email: String) {
        val intent = Intent(Intent.ACTION_SENDTO, "mailto:$email".toUri())
            .addFlags(FLAG_ACTIVITY_NEW_TASK)
        runCatching { appContext.startActivity(Intent.createChooser(intent, "Chooser Title")) }
            .onFailure { logger.e(it) }
    }
}