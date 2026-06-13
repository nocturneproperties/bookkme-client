package library.money.api

internal val mockFactory = object : CurrencyFactoryImpl {
    override fun forCode(code: String): Currency {
        return object : Currency {
            override fun format(value: Long): String {
                return value.toString()
            }

            override fun code(): String {
                return code
            }

            override fun symbol(): String {
                return code
            }

            override fun decimalSeparator(): Char {
                return '.'
            }

            override fun asString(): String {
                return code
            }

            override fun equals(other: Any?): Boolean {
                TODO("Not yet implemented")
            }

            override fun hashCode(): Int {
                TODO("Not yet implemented")
            }
        }
    }
}