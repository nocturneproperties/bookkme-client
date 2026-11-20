package library.telegram

import com.varabyte.kobweb.silk.theme.colors.ColorMode
import kotlinx.browser.window
import me.bookk.site.designsystem.resources.AppLanguage

class TelegramSession(
    val colorMode: ColorMode,
    val language: AppLanguage,
) {
    companion object {
        private const val DARK_SCHEME = "dark"

        fun from(
            colorScheme: String,
            languageCode: String?,
            browserLanguages: List<String>,
        ): TelegramSession {
            return TelegramSession(
                colorMode = colorMode(colorScheme),
                language = AppLanguage.resolve(languageCode, browserLanguages),
            )
        }

        private fun colorMode(colorScheme: String): ColorMode {
            return if (colorScheme == DARK_SCHEME) ColorMode.DARK else ColorMode.LIGHT
        }
    }
}

fun TelegramWebApp.session(): TelegramSession {
    return TelegramSession.from(
        colorScheme = colorScheme,
        languageCode = initDataUnsafe.user?.languageCode,
        browserLanguages = window.navigator.languages.toList(),
    )
}
