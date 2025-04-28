package domain.krw

import java.math.BigDecimal

@JvmInline
value class CurrencyTranslationProfit private constructor(
    val value: BigDecimal,
) {

    companion object {
        fun payee(
            krwTotalPayment: KrwTotalPayment,
            krwRoundedNeighboringCopyrightFee: BigDecimal,
        ): CurrencyTranslationProfit {
            return CurrencyTranslationProfit(krwTotalPayment.value - krwRoundedNeighboringCopyrightFee)
        }

        fun payer(
            krwNetPayment: KrwNetPayment,
            krwRoundedNeighboringCopyrightFee: BigDecimal,
        ): CurrencyTranslationProfit {
            return CurrencyTranslationProfit(krwNetPayment.value - krwRoundedNeighboringCopyrightFee)
        }
    }
}
