package library.telegram

import kotlinx.browser.document
import kotlinx.browser.window
import org.w3c.dom.HTMLScriptElement
import org.w3c.dom.events.Event
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object TelegramSdk {
    private const val SCRIPT_URL = "https://telegram.org/js/telegram-web-app.js"

    private var launchHash = ""

    private val loadedWebApp: TelegramWebApp?
        get() = window.asDynamic().Telegram?.WebApp.unsafeCast<TelegramWebApp?>()

    fun rememberLaunchHash() {
        launchHash = window.location.hash
    }

    suspend fun launch(): TelegramLaunchState {
        if (!TelegramLaunch.isFromTelegram(launchHash)) return TelegramLaunchState.OutsideTelegram
        restoreLaunchHash()
        val webApp = loadedWebApp ?: loadWebApp() ?: return TelegramLaunchState.OutsideTelegram
        return TelegramLaunchState.InsideTelegram(webApp)
    }

    private fun restoreLaunchHash() {
        val url = window.location.pathname + window.location.search + launchHash
        window.history.replaceState(window.history.state, "", url)
    }

    private suspend fun loadWebApp(): TelegramWebApp? {
        appendScript()
        return loadedWebApp
    }

    private suspend fun appendScript() = suspendCoroutine { continuation ->
        val script = document.createElement("script") as HTMLScriptElement
        val onSettled: (Event) -> Unit = { continuation.resume(Unit) }
        script.addEventListener("load", onSettled)
        script.addEventListener("error", onSettled)
        script.src = SCRIPT_URL
        document.head?.appendChild(script)
    }
}
