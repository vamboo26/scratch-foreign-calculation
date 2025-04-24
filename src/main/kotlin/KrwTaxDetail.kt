import java.math.BigDecimal
import java.math.RoundingMode

@Suppress("JoinDeclarationAndAssignment")
sealed interface KrwTaxDetail {

    val krwTotalPayment: BigDecimal
    val krwIncomeTax: BigDecimal
    val krwResidentTax: BigDecimal
    val krwNetPayment: BigDecimal
    val currencyTranslationProfit: BigDecimal

    class PayeeKrwTaxDetail(
        foreignTaxDetail: ForeignTaxDetail.PayeeForeignTaxDetail,
        krwRoundedNeighboringCopyrightFee: BigDecimal,
    ) : KrwTaxDetail {

        override val krwTotalPayment: BigDecimal
        override val krwIncomeTax: BigDecimal
        override val krwResidentTax: BigDecimal
        override val krwNetPayment: BigDecimal
        override val currencyTranslationProfit: BigDecimal

        /**
         * Payee 유형은 외화 `총`지급액을 기초로 하는 원화 `총`지급액 및 세금을 바탕으로 원화 `순`지급액을 계산한다.
         */
        init {
            krwTotalPayment =
                foreignTaxDetail.foreignTotalPayment.multiply(foreignTaxDetail.exchangeRate)
                    .setScaleWithRoundingHalfUp(0)

            krwIncomeTax = foreignTaxDetail.foreignIncomeTax.multiply(foreignTaxDetail.exchangeRate)
                .setScaleWithRoundingHalfUp(0)

            krwResidentTax = foreignTaxDetail.foreignResidentTax.multiply(foreignTaxDetail.exchangeRate)
                .setScaleWithRoundingHalfUp(0)

            krwNetPayment = krwTotalPayment.subtract(krwIncomeTax).subtract(krwResidentTax)

            currencyTranslationProfit = krwTotalPayment.subtract(krwRoundedNeighboringCopyrightFee)
        }
    }

    class PayerKrwTaxDetail(
        foreignTaxDetail: ForeignTaxDetail.PayerForeignTaxDetail,
        krwRoundedNeighboringCopyrightFee: BigDecimal,
    ) : KrwTaxDetail {

        override val krwTotalPayment: BigDecimal
        override val krwIncomeTax: BigDecimal
        override val krwResidentTax: BigDecimal
        override val krwNetPayment: BigDecimal
        override val currencyTranslationProfit: BigDecimal

        /**
         * Payer 유형은 외화 `순`지급액을 기초로 하는 원화 `순`지급액 및 세금을 바탕으로 원화 `총`지급액을 계산한다.
         */
        init {
            krwNetPayment = foreignTaxDetail.foreignNetPayment.multiply(foreignTaxDetail.exchangeRate)
                .setScaleWithRoundingHalfUp(0)

            krwIncomeTax = foreignTaxDetail.foreignIncomeTax.multiply(foreignTaxDetail.exchangeRate)
                .setScaleWithRoundingHalfUp(0)

            krwResidentTax = foreignTaxDetail.foreignResidentTax.multiply(foreignTaxDetail.exchangeRate)
                .setScaleWithRoundingHalfUp(0)

            krwTotalPayment = krwNetPayment.add(krwIncomeTax).add(krwResidentTax)

            currencyTranslationProfit = krwNetPayment.subtract(krwRoundedNeighboringCopyrightFee)
        }
    }
}

private fun BigDecimal.setScaleWithRoundingHalfUp(scale: Int): BigDecimal {
    return this.setScale(scale, RoundingMode.HALF_UP)
}
