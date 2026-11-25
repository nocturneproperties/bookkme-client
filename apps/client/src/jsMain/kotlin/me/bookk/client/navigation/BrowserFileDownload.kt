package me.bookk.client.navigation

import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.HTMLAnchorElement
import org.w3c.dom.url.URL
import org.w3c.files.Blob
import org.w3c.files.BlobPropertyBag

internal object BrowserFileDownload {
    private const val REVOKE_DELAY_MS = 60_000

    fun save(fileName: String, content: String, mimeType: String) {
        val url = URL.createObjectURL(Blob(arrayOf(content), BlobPropertyBag(type = mimeType)))
        val link = document.createElement("a") as HTMLAnchorElement
        link.href = url
        link.download = fileName
        link.style.display = "none"
        document.body?.appendChild(link)
        link.click()
        link.remove()
        window.setTimeout({ URL.revokeObjectURL(url) }, REVOKE_DELAY_MS)
    }
}
