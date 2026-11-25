package library.telegram

import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

external interface TelegramDownloadFileParams {
    var url: String

    @JsName("file_name")
    var fileName: String
}

enum class TelegramDownloadResult {
    ACCEPTED,
    DECLINED,
    UNSUPPORTED,
}

class TelegramFileDownload(
    val url: String,
    val fileName: String,
) {
    fun toParams(): TelegramDownloadFileParams {
        return js("{}").unsafeCast<TelegramDownloadFileParams>().also { params ->
            params.url = url
            params.fileName = fileName
        }
    }
}

suspend fun TelegramWebApp.download(file: TelegramFileDownload): TelegramDownloadResult {
    if (!supports(TelegramFeature.DOWNLOAD_FILE)) return TelegramDownloadResult.UNSUPPORTED
    val isAccepted = suspendCancellableCoroutine { continuation ->
        downloadFile(file.toParams()) { continuation.resume(it) }
    }
    return if (isAccepted) TelegramDownloadResult.ACCEPTED else TelegramDownloadResult.DECLINED
}
