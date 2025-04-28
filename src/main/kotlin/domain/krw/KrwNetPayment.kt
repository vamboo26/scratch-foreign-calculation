package domain.krw

import domain.krw.KrwConstants.KRW_DECIMAL_SCALE
import domain.multiplyWithScale
import java.math.BigDecimal

@JvmInline
value class KrwNetPayment private constructor(
    val value: BigDecimal,
) {

    companion object {
        fun payee(
            krwTotalPayment: KrwTotalPayment,
            krwIncomeTax: KrwIncomeTax,
            krwResidentTax: KrwResidentTax,
        ): KrwNetPayment {
            return KrwNetPayment(
                krwTotalPayment.value - krwIncomeTax.value - krwResidentTax.value,
            )
        }

        fun payer(
            foreignNetPayment: BigDecimal,
            exchangeRate: BigDecimal,
        ): KrwNetPayment {
            return KrwNetPayment(foreignNetPayment.multiplyWithScale(exchangeRate, KRW_DECIMAL_SCALE))
        }
    }
}
