package domain.krw

import domain.krw.KrwConstants.KRW_DECIMAL_SCALE
import domain.multiplyWithScale
import java.math.BigDecimal

@JvmInline
value class KrwResidentTax private constructor(
    val value: BigDecimal,
) {

    companion object {
        fun calculate(
            foreignResidentTax: BigDecimal,
            exchangeRate: BigDecimal,
        ): KrwResidentTax {
            return KrwResidentTax(foreignResidentTax.multiplyWithScale(exchangeRate, KRW_DECIMAL_SCALE))
        }
    }
}
