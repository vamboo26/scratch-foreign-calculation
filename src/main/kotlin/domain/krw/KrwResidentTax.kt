package domain.krw

import domain.foreign.ForeignResidentTax
import domain.krw.KrwConstants.KRW_DECIMAL_SCALE
import domain.multiplyWithScale
import java.math.BigDecimal

@JvmInline
value class KrwResidentTax private constructor(
    val value: BigDecimal,
) {

    companion object {
        fun calculate(
            foreignResidentTax: ForeignResidentTax,
            exchangeRate: BigDecimal,
        ): KrwResidentTax {
            return KrwResidentTax(foreignResidentTax.value.multiplyWithScale(exchangeRate, KRW_DECIMAL_SCALE))
        }
    }
}
