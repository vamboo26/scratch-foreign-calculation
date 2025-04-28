package domain.foreign

import domain.divideWithScale
import domain.krw.KrwIncomeTax
import domain.krw.KrwNetPayment
import domain.krw.KrwResidentTax
import java.math.BigDecimal

@JvmInline
value class ForeignTotalPayment private constructor(
    val value: BigDecimal,
) {

    companion object {
        fun payee(
            krwRoundedNrFee: BigDecimal,
            exchangeRate: BigDecimal,
            scale: Int,
        ): ForeignTotalPayment {
            return ForeignTotalPayment(krwRoundedNrFee.divideWithScale(exchangeRate, scale))
        }

        fun payer(
            foreignNetPayment: BigDecimal,
            foreignIncomeTax: BigDecimal,
            foreignResidentTax: BigDecimal,
        ): ForeignTotalPayment {
            return ForeignTotalPayment(foreignNetPayment + foreignIncomeTax + foreignResidentTax)
        }
    }
}
