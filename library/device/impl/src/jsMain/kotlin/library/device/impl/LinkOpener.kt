package library.device.impl

internal external interface TelegramLinkHost {
    val initData: String

    fun openLink(url: String)
}

internal class LinkOpener(
    private val telegramWebApp: () -> TelegramLinkHost?,
    private val openInBrowser: (String) -> Unit,
) {

    fun open(url: String) {
        val webApp = telegramWebApp()?.takeIf { it.initData.isNotEmpty() }
        if (webApp == null) {
            openInBrowser(url)
        } else {
            webApp.openLink(url)
        }
    }
}
