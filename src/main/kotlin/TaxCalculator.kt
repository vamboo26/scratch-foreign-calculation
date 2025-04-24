import java.math.BigDecimal
import java.math.RoundingMode

sealed interface TaxCalculator {

    fun calculate(summary: Input): Output

    object PayeeTaxCalculator : TaxCalculator {
        override fun calculate(summary: Input): Output {
            val foreignAmountScaleByCurrency = if (summary.currency == "JPY") 0 else 2
            val krwRoundedNrFee = summary.neighboringCopyrightFee.setScale(0, RoundingMode.HALF_UP)
            val foreignTotalPayment =
                krwRoundedNrFee.divide(summary.exchangeRate, foreignAmountScaleByCurrency, RoundingMode.HALF_UP)
            val foreignIncomeTax = foreignTotalPayment.multiply(summary.withholdingTaxRate)
                .divide(BigDecimal(1.1), foreignAmountScaleByCurrency, RoundingMode.HALF_UP)
            val foreignResidentTax = foreignIncomeTax.multiply(BigDecimal(0.1))
                .setScale(foreignAmountScaleByCurrency, RoundingMode.HALF_UP)
            val foreignNetPayment = foreignTotalPayment.subtract(foreignIncomeTax).subtract(foreignResidentTax)
            val krwTotalPayment =
                foreignTotalPayment.multiply(summary.exchangeRate).setScale(0, RoundingMode.HALF_UP)
            val krwIncomeTax = foreignIncomeTax.multiply(summary.exchangeRate).setScale(0, RoundingMode.HALF_UP)
            val krwResidentTax = foreignResidentTax.multiply(summary.exchangeRate).setScale(0, RoundingMode.HALF_UP)
            val krwNetPayment = krwTotalPayment.subtract(krwIncomeTax).subtract(krwResidentTax)
            val currencyTranslationProfit = krwTotalPayment.subtract(krwRoundedNrFee).toInt()

            return Output(
                summary.settlementMonth,
                summary.rightHolderName,
                summary.neighboringCopyrightFee,
                summary.currency,
                summary.exchangeRate,
                summary.nation,
                summary.withholdingTaxBurdenType,
                summary.withholdingTaxRate,
                krwRoundedNrFee.divide(summary.exchangeRate, foreignAmountScaleByCurrency, RoundingMode.HALF_UP),
                foreignIncomeTax,
                foreignResidentTax,
                foreignNetPayment,
                currencyTranslationProfit,
                krwRoundedNrFee,
                krwTotalPayment,
                krwIncomeTax,
                krwResidentTax,
                krwNetPayment,
            )
        }
    }

    object PayerTaxCalculator : TaxCalculator {
        override fun calculate(summary: Input): Output {
            val foreignAmountScaleByCurrency = if (summary.currency == "JPY") 0 else 2
            val krwRoundedNrFee = summary.neighboringCopyrightFee.setScale(0, RoundingMode.HALF_UP)
            val foreignNetPayment =
                krwRoundedNrFee.divide(summary.exchangeRate, foreignAmountScaleByCurrency, RoundingMode.HALF_UP)
            val foreignWithHoldingTax: BigDecimal =
                foreignNetPayment.divide(BigDecimal(0.85), foreignAmountScaleByCurrency, RoundingMode.HALF_UP)
                    .subtract(foreignNetPayment)
            val foreignIncomeTax = foreignWithHoldingTax.divide(
                BigDecimal(1.1),
                foreignAmountScaleByCurrency,
                RoundingMode.HALF_UP
            )
            val foreignResidentTax = foreignIncomeTax.multiply(BigDecimal(0.1))
                .setScale(foreignAmountScaleByCurrency, RoundingMode.HALF_UP)
            val foreignTotalPayment = foreignNetPayment.add(foreignIncomeTax).add(foreignResidentTax)
            val krwNetPayment = foreignNetPayment.multiply(summary.exchangeRate)
                .setScale(0, RoundingMode.HALF_UP)
            val krwIncomeTax = foreignIncomeTax.multiply(summary.exchangeRate)
                .setScale(0, RoundingMode.HALF_UP)
            val krwResidentTax = foreignResidentTax.multiply(summary.exchangeRate)
                .setScale(0, RoundingMode.HALF_UP)
            val krwTotalPayment = krwNetPayment.add(krwIncomeTax).add(krwResidentTax)
            val currencyTranslationProfit = krwNetPayment.subtract(krwRoundedNrFee).toInt()

            return Output(
                summary.settlementMonth,
                summary.rightHolderName,
                summary.neighboringCopyrightFee,
                summary.currency,
                summary.exchangeRate,
                summary.nation,
                summary.withholdingTaxBurdenType,
                summary.withholdingTaxRate,
                foreignTotalPayment,
                foreignIncomeTax,
                foreignResidentTax,
                foreignNetPayment,
                currencyTranslationProfit,
                krwRoundedNrFee,
                krwTotalPayment,
                krwIncomeTax,
                krwResidentTax,
                krwNetPayment,
            )
        }
    }
}
