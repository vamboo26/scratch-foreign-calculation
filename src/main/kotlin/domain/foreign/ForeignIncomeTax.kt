package domain.foreign

import domain.divideWithScale
import domain.multiplyWithScale
import java.math.BigDecimal

@JvmInline
value class ForeignIncomeTax private constructor(
    val value: BigDecimal,
) {

    companion object {

        private val INCOME_TAX_DIVISOR = BigDecimal("1.1")

        fun payee(
            foreignTotalPayment: ForeignTotalPayment,
            withholdingTaxRate: BigDecimal,
            scale: Int,
        ): ForeignIncomeTax {
            return ForeignIncomeTax(
                foreignTotalPayment.value
                    .multiplyWithScale(withholdingTaxRate, scale)
                    .divideWithScale(INCOME_TAX_DIVISOR, scale)
            )
        }

        fun payer(foreignWithholdingTax: BigDecimal, scale: Int): ForeignIncomeTax {
            return ForeignIncomeTax(
                foreignWithholdingTax
                    .divideWithScale(INCOME_TAX_DIVISOR, scale)
            )
        }
    }
}
