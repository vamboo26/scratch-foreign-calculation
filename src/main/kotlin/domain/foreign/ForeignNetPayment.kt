package domain.foreign

import domain.divideWithScale
import java.math.BigDecimal

@JvmInline
value class ForeignNetPayment private constructor(
    val value: BigDecimal,
) {

    companion object {

        fun payee(
            foreignTotalPayment: ForeignTotalPayment,
            foreignIncomeTax: ForeignIncomeTax,
            foreignResidentTax: ForeignResidentTax,
        ): ForeignNetPayment {
            return ForeignNetPayment(foreignTotalPayment.value - foreignIncomeTax.value - foreignResidentTax.value)
        }

        fun payer(krwRoundedNrFee: BigDecimal, exchangeRate: BigDecimal, scale: Int): ForeignNetPayment {
            return ForeignNetPayment(krwRoundedNrFee.divideWithScale(exchangeRate, scale))
        }
    }
}
