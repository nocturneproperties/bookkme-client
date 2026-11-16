package me.bookk.site.designsystem.resources

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.provider.JsStringProvider
import dev.icerock.moko.resources.provider.RemoteJsStringLoader
import kotlinx.browser.document
import kotlinx.browser.localStorage
import kotlinx.browser.window
import me.bookk.android.core.presentation.CorePresentation
import me.bookk.android.designsystem.resources.DesignSystem

object WebLocalization {
    private const val LANGUAGE_KEY = "bookkme:language"

    var language: AppLanguage by mutableStateOf(AppLanguage.DEFAULT)
        private set

    internal var provider: JsStringProvider? by mutableStateOf(null)
        private set

    fun restoreLanguage() {
        val saved = runCatching { localStorage.getItem(LANGUAGE_KEY) }.getOrNull()
        apply(AppLanguage.resolve(saved, window.navigator.languages.toList()))
    }

    fun select(language: AppLanguage) {
        runCatching { localStorage.setItem(LANGUAGE_KEY, language.tag) }
        apply(language)
    }

    suspend fun load(appStrings: RemoteJsStringLoader) {
        val sharedStrings = DesignSystem.stringsLoader + CorePresentation.strings.stringsLoader
        provider = (appStrings + sharedStrings).getOrLoad()
    }

    internal fun resolve(desc: StringDesc): String {
        return provider?.let(desc::toLocalizedString).orEmpty()
    }

    private fun apply(language: AppLanguage) {
        StringDesc.localeType = StringDesc.LocaleType.Custom(language.tag)
        document.documentElement?.setAttribute("lang", language.tag)
        this.language = language
    }
}
