package me.bookk.feature.settings.presentation.export

import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.HTMLAnchorElement
import org.w3c.dom.url.URL
import org.w3c.files.Blob
import org.w3c.files.BlobPropertyBag
import org.w3c.files.File
import org.w3c.files.FilePropertyBag
import kotlin.js.json

internal class BrowserNativeFileShare(
    private val navigator: dynamic,
) : NativeFileShare {

    override fun canShare(file: ExportFile): Boolean {
        return navigator.canShare(shareData(file)) == true
    }

    override fun share(file: ExportFile) {
        navigator.share(shareData(file)).`catch` { _: dynamic -> }
    }

    private fun shareData(file: ExportFile): dynamic {
        val shared = File(
            arrayOf(file.content),
            file.name,
            FilePropertyBag(type = JSON_MIME_TYPE),
        )
        return json("files" to arrayOf(shared))
    }

    companion object {
        fun fromBrowser(): NativeFileShare? {
            val navigator = window.navigator.asDynamic()
            val canShareFiles = navigator.share != undefined && navigator.canShare != undefined
            return if (canShareFiles) BrowserNativeFileShare(navigator) else null
        }
    }
}

internal object BrowserFileDownload {
    private const val REVOKE_DELAY_MS = 60_000

    fun save(file: ExportFile) {
        val blob = Blob(arrayOf(file.content), BlobPropertyBag(type = JSON_MIME_TYPE))
        val url = URL.createObjectURL(blob)
        val link = document.createElement("a") as HTMLAnchorElement
        link.href = url
        link.download = file.name
        link.style.display = "none"
        document.body?.appendChild(link)
        link.click()
        link.remove()
        window.setTimeout({ URL.revokeObjectURL(url) }, REVOKE_DELAY_MS)
    }
}

private const val JSON_MIME_TYPE = "application/json"
