package me.bookk.core.text

import java.text.Normalizer

internal actual fun String.canonicallyDecomposed(): String {
    return Normalizer.normalize(this, Normalizer.Form.NFD)
}
