package me.bookk.feature.settings.presentation.export

internal data class ExportFile(
    val name: String,
    val content: String,
)

internal interface NativeFileShare {
    fun canShare(file: ExportFile): Boolean

    fun share(file: ExportFile)
}

internal class ExportFileSharer(
    private val nativeShare: () -> NativeFileShare?,
    private val download: (ExportFile) -> Unit,
) {

    fun share(file: ExportFile) {
        val native = nativeShare()?.takeIf { it.canShare(file) }
        if (native == null) {
            download(file)
        } else {
            native.share(file)
        }
    }
}
