package domain.foreign

import domain.multiplyWithScale
import java.math.BigDecimal

@JvmInline
value class ForeignResidentTax private constructor(
    val value: BigDecimal,
) {

    companion object {

        private val RESIDENT_TAX_MULTIPLIER = BigDecimal("0.1")

        fun calculate(foreignIncomeTax: ForeignIncomeTax, scale: Int): ForeignResidentTax {
            return ForeignResidentTax(
                foreignIncomeTax.value.multiplyWithScale(RESIDENT_TAX_MULTIPLIER, scale)
            )
        }
    }
}
