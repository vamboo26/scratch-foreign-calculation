enum class CurrencyScale(
    val scale: Int,
) {
    JPY(0),
    USD(2),
    EUR(2),
    ;

    companion object {
        fun fromCurrency(currency: String): CurrencyScale {
            return when (currency.uppercase()) {
                "JPY" -> JPY
                "USD" -> USD
                "EUR" -> EUR
                else -> throw IllegalArgumentException("Unsupported currency: $currency")
            }
        }
    }
}
