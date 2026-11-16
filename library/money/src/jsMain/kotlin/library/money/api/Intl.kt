package library.money.api

internal external object Intl {
    class NumberFormat(
        locales: String? = definedExternally,
        options: dynamic = definedExternally,
    ) {
        fun format(value: Double): String
        fun formatToParts(value: Double): Array<NumberFormatPart>
    }
}

internal external interface NumberFormatPart {
    val type: String
    val value: String
}
