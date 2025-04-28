package domain.krw

import domain.krw.KrwConstants.KRW_DECIMAL_SCALE
import domain.multiplyWithScale
import java.math.BigDecimal

@JvmInline
value class KrwTotalPayment private constructor(
    val value: BigDecimal,
) {

    companion object {
        fun payee(
            foreignTotalPayment: BigDecimal,
            exchangeRate: BigDecimal,
        ): KrwTotalPayment {
            return KrwTotalPayment(foreignTotalPayment.multiplyWithScale(exchangeRate, KRW_DECIMAL_SCALE))
        }

        fun payer(
            krwNetPayment: BigDecimal,
            krwIncomeTax: BigDecimal,
            krwResidentTax: BigDecimal,
        ): KrwTotalPayment {
            return KrwTotalPayment(krwNetPayment + krwIncomeTax + krwResidentTax)
        }
    }
}
