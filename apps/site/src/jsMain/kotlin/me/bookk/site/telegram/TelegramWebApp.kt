package me.bookk.site.telegram

external interface TelegramWebApp {
    val colorScheme: String
    val initDataUnsafe: TelegramInitData

    fun ready()

    fun expand()

    fun setHeaderColor(color: String)

    fun setBackgroundColor(color: String)

    fun onEvent(eventType: String, eventHandler: () -> Unit)

    fun offEvent(eventType: String, eventHandler: () -> Unit)
}

external interface TelegramInitData {
    val user: TelegramUser?
}

external interface TelegramUser {
    @JsName("language_code")
    val languageCode: String?
}

object TelegramEvents {
    const val THEME_CHANGED = "themeChanged"
}
