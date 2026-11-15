package me.bookk.core.presentation.intl

import dev.icerock.moko.resources.desc.StringDesc

internal fun intlLocales(): Array<String> {
    val custom = StringDesc.localeType as? StringDesc.LocaleType.Custom
    return listOfNotNull(custom?.locale).toTypedArray()
}

internal fun resolvedLocaleTag(): String {
    return Intl.DateTimeFormat(
        intlLocales(),
        js("({})"),
    ).asDynamic().resolvedOptions().locale as String
}

internal fun jsOptions(vararg entries: Pair<String, Any>): dynamic {
    val options: dynamic = js("({})")
    entries.forEach { (key, value) -> options[key] = value }
    return options
}
