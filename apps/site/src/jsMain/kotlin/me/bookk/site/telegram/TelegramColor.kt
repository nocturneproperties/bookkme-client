package me.bookk.site.telegram

import com.varabyte.kobweb.compose.ui.graphics.Color

private const val HEX_RADIX = 16
private const val HEX_BYTE_LENGTH = 2

internal fun Color.toTelegramColor(): String {
    val rgb = toRgb()
    return "#" + listOf(rgb.red, rgb.green, rgb.blue).joinToString("") { it.toHexByte() }
}

private fun Int.toHexByte(): String {
    return toString(HEX_RADIX).padStart(HEX_BYTE_LENGTH, '0')
}
