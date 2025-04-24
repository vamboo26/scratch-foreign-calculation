import java.math.BigDecimal
import java.math.RoundingMode

@Suppress("JoinDeclarationAndAssignment")
sealed interface ForeignTaxDetail {

    companion object {
        private val INCOME_TAX_DIVISOR = BigDecimal(1.1)
        private val RESIDENT_TAX_MULTIPLIER = BigDecimal(0.1)
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

        private val foreignAmountScaleByCurrency = if (currency == "JPY") 0 else 2

        override val foreignTotalPayment: BigDecimal
        override val foreignIncomeTax: BigDecimal
        override val foreignResidentTax: BigDecimal
        override val foreignNetPayment: BigDecimal

        /**
         * Payee 유형은 원화 인접권료를 기초로 외화 `총`지급액을 먼저 계산하고, 순차적으로 외화 세금과 외화 순지급액을 계산한다.
         */
        init {
            foreignTotalPayment =
                krwRoundedNrFee.divide(exchangeRate, foreignAmountScaleByCurrency, RoundingMode.HALF_UP)

            foreignIncomeTax = foreignTotalPayment.multiply(withholdingTaxRate)
                .divide(INCOME_TAX_DIVISOR, foreignAmountScaleByCurrency, RoundingMode.HALF_UP)

            foreignResidentTax = foreignIncomeTax.multiply(RESIDENT_TAX_MULTIPLIER)
                .setScaleWithRoundingHalfUp(foreignAmountScaleByCurrency)

            foreignNetPayment = foreignTotalPayment.subtract(foreignIncomeTax).subtract(foreignResidentTax)
        }
    }


    class PayerForeignTaxDetail(
        override val currency: String,
        override val exchangeRate: BigDecimal,
        krwRoundedNrFee: BigDecimal,
    ) : ForeignTaxDetail {

        private val foreignAmountScaleByCurrency = if (currency == "JPY") 0 else 2

        override val foreignTotalPayment: BigDecimal
        override val foreignIncomeTax: BigDecimal
        override val foreignResidentTax: BigDecimal
        override val foreignNetPayment: BigDecimal

        /**
         * Payer 유형은 원화 인접권료를 기초로 외화 `순`지급액을 먼저 계산하고, 외화 세금과 외화 총지급액을 역산한다.
         */
        init {
            foreignNetPayment = krwRoundedNrFee.divide(exchangeRate, foreignAmountScaleByCurrency, RoundingMode.HALF_UP)

            val foreignWithHoldingTax: BigDecimal =
                foreignNetPayment.divide(BigDecimal(0.85), foreignAmountScaleByCurrency, RoundingMode.HALF_UP)
                    .subtract(foreignNetPayment)

            foreignIncomeTax = foreignWithHoldingTax.divide(
                INCOME_TAX_DIVISOR,
                foreignAmountScaleByCurrency,
                RoundingMode.HALF_UP
            )

            foreignResidentTax = foreignIncomeTax.multiply(RESIDENT_TAX_MULTIPLIER)
                .setScaleWithRoundingHalfUp(foreignAmountScaleByCurrency)

            foreignTotalPayment = foreignNetPayment.add(foreignIncomeTax).add(foreignResidentTax)
        }
    }
}

private fun BigDecimal.setScaleWithRoundingHalfUp(scale: Int): BigDecimal {
    return this.setScale(scale, RoundingMode.HALF_UP)
}

