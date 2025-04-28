package domain.foreign

import domain.divideWithScale
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
            foreignNetPayment: ForeignNetPayment,
            foreignIncomeTax: ForeignIncomeTax,
            foreignResidentTax: ForeignResidentTax,
        ): ForeignTotalPayment {
            return ForeignTotalPayment(foreignNetPayment.value + foreignIncomeTax.value + foreignResidentTax.value)
        }
    }
}
