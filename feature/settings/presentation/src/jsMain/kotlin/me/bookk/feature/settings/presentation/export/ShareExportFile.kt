package me.bookk.feature.settings.presentation.export

import library.files.api.FileProvider
import okio.Path.Companion.toPath

internal fun shareExportFile(fileProvider: FileProvider, filePath: String) {
    val file = filePath.toPath()
    val content = fileProvider.readText(file) ?: return
    ExportFileSharer(
        nativeShare = BrowserNativeFileShare::fromBrowser,
        download = BrowserFileDownload::save,
    ).share(ExportFile(file.name, content))
}
