package me.bookk.feature.appointments.presentation.screen.details

import library.money.api.Money

private const val CENTS_IN_UNIT = 100L

internal fun Money.toPriceInput(): String {
    val units = value / CENTS_IN_UNIT
    val cents = value % CENTS_IN_UNIT
    if (cents == 0L) return units.toString()
    return "$units.${cents.toString().padStart(2, '0')}"
}

internal fun String.toMoneyOrNull(currency: Money.SupportedCurrency): Money? {
    val amount = trim().replace(',', '.').toDoubleOrNull()?.takeIf { it.isFinite() } ?: return null
    return Money(amount, currency)
}
