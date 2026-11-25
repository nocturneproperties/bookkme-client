package me.bookk.core.text

internal actual fun String.canonicallyDecomposed(): String {
    return asDynamic().normalize("NFD").unsafeCast<String>()
}
