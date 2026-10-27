package me.bookk.core.presentation.navigation

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.compose.ui.platform.UriHandler
import dev.icerock.moko.resources.desc.StringDesc

fun UriHandler.mailTo(
    email: String,
    subject: String? = null,
    body: String? = null
) {
    val uri = Uri.Builder().apply {
        scheme("mailto:$email")
        if (subject != null) {
            appendQueryParameter("subject", subject)
        }
        if (body != null) {
            appendQueryParameter("body", body)
        }
    }.build()

    openUri(uri.toString())
}

fun Context.openNotificationsSettings() {
    val intent = Intent().apply {
        action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
        putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
    }
    startActivity(intent)
}

fun Context.showToast(
    message: StringDesc,
    length: Int = Toast.LENGTH_SHORT
) {
    Toast.makeText(this, message.toString(this), length).show()
}