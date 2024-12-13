package me.bookk.core.presentation.file

import android.content.Context
import android.net.Uri
import java.io.File

fun File.createTempJpgFile(name: String = "IMG_${System.currentTimeMillis()}"): File {
    if (!isDirectory) {
        throw Throwable("Unable to create a temp jpg file. Root is not a directory.")
    }

    return File.createTempFile(
        name,
        ".jpg",
        this
    )
}

fun Uri.copyToFile(context: Context, file: File) {
    context.contentResolver?.openInputStream(this)?.use { input ->
        file.outputStream().use { out ->
            input.copyTo(out)
        }
    }
}