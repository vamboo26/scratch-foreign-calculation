package domain.foreign

import domain.CurrencyScale
import domain.divideWithScale
import java.math.BigDecimal

@Suppress("JoinDeclarationAndAssignment")
sealed interface ForeignTaxDetail {

    val currency: String
    val exchangeRate: BigDecimal

    val foreignTotalPayment: ForeignTotalPayment
    val foreignIncomeTax: ForeignIncomeTax
    val foreignResidentTax: ForeignResidentTax
    val foreignNetPayment: BigDecimal

    class PayeeForeignTaxDetail(
        override val currency: String,
        override val exchangeRate: BigDecimal,
        krwRoundedNrFee: BigDecimal,
        withholdingTaxRate: BigDecimal,
    ) : ForeignTaxDetail {

        private val scale = CurrencyScale.fromCurrency(currency).scale

        override val foreignTotalPayment: ForeignTotalPayment
        override val foreignIncomeTax: ForeignIncomeTax
        override val foreignResidentTax: ForeignResidentTax
        override val foreignNetPayment: BigDecimal

        /**
         * Payee 유형은 원화 인접권료를 기초로 외화 `총`지급액을 먼저 계산하고, 순차적으로 외화 세금과 외화 순지급액을 계산한다.
         */
        init {
            foreignTotalPayment = ForeignTotalPayment.payee(krwRoundedNrFee, exchangeRate, scale)

            foreignIncomeTax = ForeignIncomeTax.payee(
                foreignTotalPayment,
                withholdingTaxRate,
                scale,
            )

            foreignResidentTax = ForeignResidentTax.calculate(foreignIncomeTax, scale)

            foreignNetPayment = foreignTotalPayment.value - foreignIncomeTax.value - foreignResidentTax.value
        }
    }

    class PayerForeignTaxDetail(
        override val currency: String,
        override val exchangeRate: BigDecimal,
        krwRoundedNrFee: BigDecimal,
    ) : ForeignTaxDetail {

        companion object {
            private val WITHHOLDING_TAX_DIVISOR = BigDecimal("0.85")
        }

        private val scale = CurrencyScale.fromCurrency(currency).scale

        override val foreignTotalPayment: ForeignTotalPayment
        override val foreignIncomeTax: ForeignIncomeTax
        override val foreignResidentTax: ForeignResidentTax
        override val foreignNetPayment: BigDecimal

        /**
         * Payer 유형은 원화 인접권료를 기초로 외화 `순`지급액을 먼저 계산하고, 외화 세금과 외화 총지급액을 역산한다.
         */
        init {
            foreignNetPayment = krwRoundedNrFee.divideWithScale(exchangeRate, scale)

            val foreignPaymentBeforeTax = foreignNetPayment.divideWithScale(WITHHOLDING_TAX_DIVISOR, scale)

            val foreignWithholdingTax = foreignPaymentBeforeTax - foreignNetPayment

            foreignIncomeTax = ForeignIncomeTax.payer(
                foreignWithholdingTax,
                scale,
            )

            foreignResidentTax = ForeignResidentTax.calculate(foreignIncomeTax, scale)

            foreignTotalPayment =
                ForeignTotalPayment.payer(foreignNetPayment, foreignIncomeTax.value, foreignResidentTax.value)
        }
    }
}
