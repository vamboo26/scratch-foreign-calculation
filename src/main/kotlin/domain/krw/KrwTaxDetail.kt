package domain

import domain.krw.KrwIncomeTax
import domain.krw.KrwTotalPayment
import java.math.BigDecimal

@Suppress("JoinDeclarationAndAssignment")
sealed interface KrwTaxDetail {

    val krwTotalPayment: KrwTotalPayment
    val krwIncomeTax: KrwIncomeTax
    val krwResidentTax: BigDecimal
    val krwNetPayment: BigDecimal
    val currencyTranslationProfit: BigDecimal

    class PayeeKrwTaxDetail(
        foreignTaxDetail: ForeignTaxDetail.PayeeForeignTaxDetail,
        krwRoundedNeighboringCopyrightFee: BigDecimal,
    ) : KrwTaxDetail {

        override val krwTotalPayment: KrwTotalPayment
        override val krwIncomeTax: KrwIncomeTax
        override val krwResidentTax: BigDecimal
        override val krwNetPayment: BigDecimal
        override val currencyTranslationProfit: BigDecimal

        /**
         * Payee 유형은 외화 `총`지급액을 기초로 하는 원화 `총`지급액 및 세금을 바탕으로 원화 `순`지급액을 계산한다.
         */
        init {
            krwTotalPayment = KrwTotalPayment.payee(foreignTaxDetail.foreignTotalPayment, foreignTaxDetail.exchangeRate)

            krwIncomeTax = KrwIncomeTax.calculate(foreignTaxDetail.foreignIncomeTax, foreignTaxDetail.exchangeRate)

            krwResidentTax = foreignTaxDetail.foreignResidentTax.multiplyWithScale(foreignTaxDetail.exchangeRate, 0)

            krwNetPayment = krwTotalPayment.value - krwIncomeTax.value - krwResidentTax

            currencyTranslationProfit = krwTotalPayment.value - krwRoundedNeighboringCopyrightFee
        }
    }

    class PayerKrwTaxDetail(
        foreignTaxDetail: ForeignTaxDetail.PayerForeignTaxDetail,
        krwRoundedNeighboringCopyrightFee: BigDecimal,
    ) : KrwTaxDetail {

        override val krwTotalPayment: KrwTotalPayment
        override val krwIncomeTax: KrwIncomeTax
        override val krwResidentTax: BigDecimal
        override val krwNetPayment: BigDecimal
        override val currencyTranslationProfit: BigDecimal

        /**
         * Payer 유형은 외화 `순`지급액을 기초로 하는 원화 `순`지급액 및 세금을 바탕으로 원화 `총`지급액을 계산한다.
         */
        init {
            krwNetPayment = foreignTaxDetail.foreignNetPayment.multiplyWithScale(foreignTaxDetail.exchangeRate, 0)

            krwIncomeTax = KrwIncomeTax.calculate(foreignTaxDetail.foreignIncomeTax, foreignTaxDetail.exchangeRate)

            krwResidentTax = foreignTaxDetail.foreignResidentTax.multiplyWithScale(foreignTaxDetail.exchangeRate, 0)

            krwTotalPayment = KrwTotalPayment.payer(krwNetPayment, krwIncomeTax.value, krwResidentTax)

            currencyTranslationProfit = krwNetPayment - krwRoundedNeighboringCopyrightFee
        }
    }
}
