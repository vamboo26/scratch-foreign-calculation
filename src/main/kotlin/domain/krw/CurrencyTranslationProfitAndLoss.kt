package domain.krw

import java.math.BigDecimal

@JvmInline
value class CurrencyTranslationProfitAndLoss private constructor(
    val value: BigDecimal,
) {

    companion object {
        fun payee(
            krwTotalPayment: KrwTotalPayment,
            krwRoundedNeighboringCopyrightFee: BigDecimal,
        ): CurrencyTranslationProfitAndLoss {
            return CurrencyTranslationProfitAndLoss(krwTotalPayment.value - krwRoundedNeighboringCopyrightFee)
        }

        fun payer(
            krwNetPayment: KrwNetPayment,
            krwRoundedNeighboringCopyrightFee: BigDecimal,
        ): CurrencyTranslationProfitAndLoss {
            return CurrencyTranslationProfitAndLoss(krwNetPayment.value - krwRoundedNeighboringCopyrightFee)
        }
    }
}
