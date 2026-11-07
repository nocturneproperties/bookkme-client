package me.bookk.feature.business.presentation.screen.export

import android.content.ClipData
import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import java.io.File

internal class BusinessDataExportFileProvider : FileProvider()

internal fun Context.shareBusinessExportFile(filePath: String) {
    val file = File(filePath)
    val uri = FileProvider.getUriForFile(this, "$packageName.business_data_exports", file)
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = JSON_MIME_TYPE
        putExtra(Intent.EXTRA_STREAM, uri)
        clipData = ClipData.newRawUri(file.name, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
    startActivity(Intent.createChooser(intent, null))
}

private const val JSON_MIME_TYPE = "application/json"
