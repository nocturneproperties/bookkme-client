package library.telegram

object TelegramLaunch {
    private const val WEB_APP_DATA_PARAM = "tgWebAppData"

    fun isFromTelegram(hash: String): Boolean {
        return hash
            .removePrefix("#")
            .split('&')
            .any { it.substringBefore('=') == WEB_APP_DATA_PARAM }
    }
}
