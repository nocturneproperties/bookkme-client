package library.money.api

actual class Currency actual constructor(
    private val code: String,
) {
    private val formatter = Intl.NumberFormat(options = currencyOptions(code))

    private val symbol = formatter.part("currency") ?: code

    actual fun code(): String {
        return code
    }

    actual fun format(value: Long): String {
        return formatter.format(value / 100.0)
    }

    actual fun symbol(): String {
        return symbol
    }

    actual fun decimalSeparator(): Char {
        return Intl.NumberFormat().part("decimal")?.firstOrNull() ?: '.'
    }

    actual fun asString(): String {
        return "$code ($symbol)"
    }

    actual override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (other !is Currency) return false
        return other.code() == code()
    }

    actual override fun hashCode(): Int {
        return code().hashCode()
    }
}

private fun currencyOptions(code: String): dynamic {
    val options: dynamic = js("({})")
    options.style = "currency"
    options.currency = code
    return options
}

private fun Intl.NumberFormat.part(type: String): String? {
    return formatToParts(1.5).firstOrNull { it.type == type }?.value
}
