package domain.krw

import domain.foreign.ForeignTotalPayment
import domain.krw.KrwConstants.KRW_DECIMAL_SCALE
import domain.multiplyWithScale
import java.math.BigDecimal

@JvmInline
value class KrwTotalPayment private constructor(
    val value: BigDecimal,
) {

    companion object {
        fun payee(
            foreignTotalPayment: ForeignTotalPayment,
            exchangeRate: BigDecimal,
        ): KrwTotalPayment {
            return KrwTotalPayment(foreignTotalPayment.value.multiplyWithScale(exchangeRate, KRW_DECIMAL_SCALE))
        }

        fun payer(
            krwNetPayment: KrwNetPayment,
            krwIncomeTax: KrwIncomeTax,
            krwResidentTax: KrwResidentTax,
        ): KrwTotalPayment {
            return KrwTotalPayment(krwNetPayment.value + krwIncomeTax.value + krwResidentTax.value)
        }
    }
}
