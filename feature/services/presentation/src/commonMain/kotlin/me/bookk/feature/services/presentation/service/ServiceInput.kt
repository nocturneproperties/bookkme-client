package me.bookk.feature.services.presentation.service

private val PRICE_SEPARATORS = listOf('.', ',')

internal fun String.toDurationInput(): String {
    return filter { it.isDigit() }
}

internal fun String.toPriceInput(): String {
    return filter { it.isDigit() || it in PRICE_SEPARATORS }.replace(",", ".")
}
