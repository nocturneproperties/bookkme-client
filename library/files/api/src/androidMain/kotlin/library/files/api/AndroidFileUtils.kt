package library.files.api

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import kotlinx.coroutines.withContext
import me.bookk.core.coroutine.DispatcherProvider


suspend fun getFileWithName(uri: Uri, context: Context): Result<FileBytes> {
    return runCatching {
        val inputStream = context.contentResolver.openInputStream(uri) ?: throw IllegalStateException("Failed to open InputStream from uri: $uri")
        val (name, base64Data) = withContext(DispatcherProvider.io) {
            inputStream.use {
                getFileNameFromUri(uri, context) to it.readBytes()
            }
        }
        FileBytes(name, base64Data)
    }
}

fun getFileNameFromUri(uri: Uri, context: Context): String {
    var fileName: String? = null
    context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
        if (cursor.moveToFirst()) {
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (nameIndex >= 0) {
                fileName = cursor.getString(nameIndex)
            }
        }
    }
    return fileName ?: uri.lastPathSegment ?: "file"
}