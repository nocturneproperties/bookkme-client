package library.telegram

external interface TelegramWebApp {
    val colorScheme: String
    val initData: String
    val initDataUnsafe: TelegramInitData

    @JsName("MainButton")
    val mainButton: TelegramBottomButton

    @JsName("SecondaryButton")
    val secondaryButton: TelegramBottomButton

    @JsName("BackButton")
    val backButton: TelegramBackButton

    @JsName("SettingsButton")
    val settingsButton: TelegramSettingsButton

    @JsName("HapticFeedback")
    val hapticFeedback: TelegramHapticFeedback

    fun isVersionAtLeast(version: String): Boolean

    fun ready()

    fun expand()

    fun openLink(url: String)

    fun setHeaderColor(color: String)

    fun setBackgroundColor(color: String)

    fun setBottomBarColor(color: String)

    fun showPopup(params: TelegramPopupParams, callback: (String?) -> Unit)

    fun downloadFile(params: TelegramDownloadFileParams, callback: (Boolean) -> Unit)

    fun requestWriteAccess(callback: (Boolean) -> Unit)

    fun onEvent(eventType: String, eventHandler: () -> Unit)

    fun offEvent(eventType: String, eventHandler: () -> Unit)
}

external interface TelegramInitData {
    val user: TelegramUser?

    @JsName("start_param")
    val startParam: String?

    val hash: String?
}

external interface TelegramUser {
    @JsName("language_code")
    val languageCode: String?

    @JsName("photo_url")
    val photoUrl: String?
}

external interface TelegramBackButton {
    fun show(): TelegramBackButton

    fun hide(): TelegramBackButton

    fun onClick(callback: () -> Unit): TelegramBackButton

    fun offClick(callback: () -> Unit): TelegramBackButton
}

external interface TelegramSettingsButton {
    fun show(): TelegramSettingsButton

    fun hide(): TelegramSettingsButton

    fun onClick(callback: () -> Unit): TelegramSettingsButton

    fun offClick(callback: () -> Unit): TelegramSettingsButton
}

external interface TelegramHapticFeedback {
    fun notificationOccurred(type: String): TelegramHapticFeedback
}

object TelegramEvents {
    const val THEME_CHANGED = "themeChanged"
    const val ACTIVATED = "activated"
}

object TelegramHaptics {
    const val SUCCESS = "success"
}
