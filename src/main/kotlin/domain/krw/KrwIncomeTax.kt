package domain.krw

import domain.krw.KrwConstants.KRW_DECIMAL_SCALE
import domain.multiplyWithScale
import java.math.BigDecimal

@JvmInline
value class KrwIncomeTax private constructor(
    val value: BigDecimal,
) {

    companion object {
        fun calculate(
            foreignIncomeTax: BigDecimal,
            exchangeRate: BigDecimal,
        ): KrwIncomeTax {
            return KrwIncomeTax(foreignIncomeTax.multiplyWithScale(exchangeRate, KRW_DECIMAL_SCALE))
        }
    }
}
