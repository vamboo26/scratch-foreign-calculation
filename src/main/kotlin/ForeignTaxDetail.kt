import java.math.BigDecimal

@Suppress("JoinDeclarationAndAssignment")
sealed interface ForeignTaxDetail {

    companion object {
        private val INCOME_TAX_DIVISOR = BigDecimal("1.1")
        private val RESIDENT_TAX_MULTIPLIER = BigDecimal("0.1")
    }

    val currency: String
    val exchangeRate: BigDecimal

    val foreignTotalPayment: BigDecimal
    val foreignIncomeTax: BigDecimal
    val foreignResidentTax: BigDecimal
    val foreignNetPayment: BigDecimal

    class PayeeForeignTaxDetail(
        override val currency: String,
        override val exchangeRate: BigDecimal,
        krwRoundedNrFee: BigDecimal,
        withholdingTaxRate: BigDecimal,
    ) : ForeignTaxDetail {

        private val scale = CurrencyScale.fromCurrency(currency).scale

        override val foreignTotalPayment: BigDecimal
        override val foreignIncomeTax: BigDecimal
        override val foreignResidentTax: BigDecimal
        override val foreignNetPayment: BigDecimal

        /**
         * Payee 유형은 원화 인접권료를 기초로 외화 `총`지급액을 먼저 계산하고, 순차적으로 외화 세금과 외화 순지급액을 계산한다.
         */
        init {
            foreignTotalPayment = krwRoundedNrFee.divideWithScale(exchangeRate, scale)

            foreignIncomeTax = foreignTotalPayment
                .multiplyWithScale(withholdingTaxRate, scale)
                .divideWithScale(INCOME_TAX_DIVISOR, scale)

            foreignResidentTax = foreignIncomeTax.multiplyWithScale(RESIDENT_TAX_MULTIPLIER, scale)

            foreignNetPayment = foreignTotalPayment.subtract(foreignIncomeTax).subtract(foreignResidentTax)
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

        override val foreignTotalPayment: BigDecimal
        override val foreignIncomeTax: BigDecimal
        override val foreignResidentTax: BigDecimal
        override val foreignNetPayment: BigDecimal

        /**
         * Payer 유형은 원화 인접권료를 기초로 외화 `순`지급액을 먼저 계산하고, 외화 세금과 외화 총지급액을 역산한다.
         */
        init {
            foreignNetPayment = krwRoundedNrFee.divideWithScale(exchangeRate, scale)

            val foreignWithHoldingTax = foreignNetPayment
                .divideWithScale(WITHHOLDING_TAX_DIVISOR, scale)
                .subtract(foreignNetPayment)

            foreignIncomeTax = foreignWithHoldingTax.divideWithScale(INCOME_TAX_DIVISOR, scale)

            foreignResidentTax = foreignIncomeTax.multiplyWithScale(RESIDENT_TAX_MULTIPLIER, scale)

            foreignTotalPayment = foreignNetPayment.add(foreignIncomeTax).add(foreignResidentTax)
        }
    }
}

