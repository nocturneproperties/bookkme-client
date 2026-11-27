package library.telegram

import kotlinx.browser.window
import org.w3c.dom.Storage

object TelegramStartParam {
    internal const val HANDLED_LAUNCH_KEY = "bookkme_handled_launch"

    fun unhandled(startParam: String?, launchId: String?, handledLaunchId: String?): String {
        val isReload = launchId != null && launchId == handledLaunchId
        return if (isReload) "" else startParam.orEmpty()
    }
}

fun TelegramWebApp.takeStartParam(): String {
    val storage = sessionStorageOrNull()
    val launchId = initDataUnsafe.hash
    val startParam = TelegramStartParam.unhandled(
        startParam = initDataUnsafe.startParam,
        launchId = launchId,
        handledLaunchId = storage?.getItem(TelegramStartParam.HANDLED_LAUNCH_KEY),
    )
    if (launchId != null) {
        storage?.setItem(TelegramStartParam.HANDLED_LAUNCH_KEY, launchId)
    }
    return startParam
}

private fun sessionStorageOrNull(): Storage? {
    return runCatching { window.sessionStorage }.getOrNull()
}
