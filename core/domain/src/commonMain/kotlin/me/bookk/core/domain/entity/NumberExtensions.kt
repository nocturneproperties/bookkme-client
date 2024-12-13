package me.bookk.core.domain.entity

import kotlin.math.pow
import kotlin.math.roundToInt

// Note: KMM doesn't currently support formatting of decimal numbers.
// Feature request: https://youtrack.jetbrains.com/issue/KT-21644

/**
 * Converts the float value to a string with the specified number of decimal places.
 * If the float value has no decimal part, it returns the integer part as a string:
 * ```
 * 12.3456f.toString(2) // 12.35
 * 12f.toString(2) // 12
 * ```
 * @param decimalPlaces The number of decimal places to include in the string.
 * @return The string representation of the float value with the specified number of decimal places.
 * @throws IllegalArgumentException if [decimalPlaces] is negative.
 */
fun Float.toString(decimalPlaces: Int): String {
    return if (round(1) % 1 == 0f) {
        roundToInt().toString()
    } else {
        round(decimalPlaces).toString()
    }
}

/**
 * Rounds the float value to the specified number of decimal places. Example:
 * ```
 * val value = 12.345678f
 * val roundedValue = value.round(2) // roundedValue = 12.35
 * ```
 * @param decimalPlaces The number of decimal places to round to. Defaults to 2.
 * @return The rounded float value.
 * @throws IllegalArgumentException if [decimalPlaces] is negative.
 */
fun Float.round(decimalPlaces: Int = 2): Float {
    require(decimalPlaces >= 0) { "Decimal places cannot be negative" }
    val multiplier = 10.0f.pow(decimalPlaces)
    return (this * multiplier).roundToInt() / multiplier
}
