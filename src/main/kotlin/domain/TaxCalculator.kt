package domain

import Input
import Output
import java.math.RoundingMode

sealed interface TaxCalculator {

    fun calculate(summary: Input): Output

    object PayeeTaxCalculator : TaxCalculator {
        override fun calculate(summary: Input): Output {

            val krwRoundedNrFee = summary.neighboringCopyrightFee.setScale(0, RoundingMode.HALF_UP)
            val foreignTaxDetail = ForeignTaxDetail.PayeeForeignTaxDetail(
                summary.currency,
                summary.exchangeRate,
                krwRoundedNrFee,
                summary.withholdingTaxRate
            )
            val krwTaxDetail = KrwTaxDetail.PayeeKrwTaxDetail(foreignTaxDetail, krwRoundedNrFee)

            return Output(
                summary.settlementMonth,
                summary.rightHolderName,
                summary.neighboringCopyrightFee,
                summary.currency,
                summary.exchangeRate,
                summary.nation,
                summary.withholdingTaxBurdenType,
                summary.withholdingTaxRate,
                foreignTaxDetail.foreignTotalPayment,
                foreignTaxDetail.foreignIncomeTax,
                foreignTaxDetail.foreignResidentTax,
                foreignTaxDetail.foreignNetPayment,
                krwTaxDetail.currencyTranslationProfit.toInt(),
                krwRoundedNrFee,
                krwTaxDetail.krwTotalPayment.value,
                krwTaxDetail.krwIncomeTax.value,
                krwTaxDetail.krwResidentTax.value,
                krwTaxDetail.krwNetPayment.value,
            )
        }
    }

    object PayerTaxCalculator : TaxCalculator {
        override fun calculate(summary: Input): Output {

            val krwRoundedNrFee = summary.neighboringCopyrightFee.setScale(0, RoundingMode.HALF_UP)
            val foreignTaxDetail = ForeignTaxDetail.PayerForeignTaxDetail(
                summary.currency,
                summary.exchangeRate,
                krwRoundedNrFee,
            )
            val krwTaxDetail = KrwTaxDetail.PayerKrwTaxDetail(foreignTaxDetail, krwRoundedNrFee)

            return Output(
                summary.settlementMonth,
                summary.rightHolderName,
                summary.neighboringCopyrightFee,
                summary.currency,
                summary.exchangeRate,
                summary.nation,
                summary.withholdingTaxBurdenType,
                summary.withholdingTaxRate,
                foreignTaxDetail.foreignTotalPayment,
                foreignTaxDetail.foreignIncomeTax,
                foreignTaxDetail.foreignResidentTax,
                foreignTaxDetail.foreignNetPayment,
                krwTaxDetail.currencyTranslationProfit.toInt(),
                krwRoundedNrFee,
                krwTaxDetail.krwTotalPayment.value,
                krwTaxDetail.krwIncomeTax.value,
                krwTaxDetail.krwResidentTax.value,
                krwTaxDetail.krwNetPayment.value,
            )
        }
    }
}
