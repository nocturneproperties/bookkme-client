package library.money.api

internal val mockFactory = object : CurrencyFactoryImpl {
    override fun forCode(code: String): Currency {
        return object : Currency {
            override fun format(value: Long): String {
                return value.toString()
            }

            override fun symbol(): String {
                return code
            }

            override fun asString(): String {
                return code
            }
        }
    }
}