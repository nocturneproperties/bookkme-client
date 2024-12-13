package me.bookk.build_src.localise

class LocaliseConfig {
    var locoBaseUrl: String = "https://localise.biz/api/export/locale"
    var apiKey: String = ""
    var languages: Array<String> = arrayOf("en")
    var defaultLang: String = "en"
    var resDir: String = ""
    var fileName: String = "strings"
    var placeholderPattern: String? = null
    var hideComments: Boolean = false
    var format: String = "android"
    var tag: String? = null
    var fallbackLang: String? = null
    var orderByAssetId: Boolean = false
    var status: String? = null
    var saveDefLangDuplicate: Boolean = false
    var resourceNamePrefix: String? = null
    var ignoreMissingTranslationWarnings: Boolean = false
    var index: Int? = null
}