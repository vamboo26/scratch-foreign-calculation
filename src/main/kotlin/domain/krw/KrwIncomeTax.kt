package domain.krw

import domain.foreign.ForeignIncomeTax
import domain.krw.KrwConstants.KRW_DECIMAL_SCALE
import domain.multiplyWithScale
import java.math.BigDecimal

@JvmInline
value class KrwIncomeTax private constructor(
    val value: BigDecimal,
) {

    companion object {
        fun calculate(
            foreignIncomeTax: ForeignIncomeTax,
            exchangeRate: BigDecimal,
        ): KrwIncomeTax {
            return KrwIncomeTax(foreignIncomeTax.value.multiplyWithScale(exchangeRate, KRW_DECIMAL_SCALE))
        }
    }
}
