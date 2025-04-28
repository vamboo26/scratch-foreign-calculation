package domain

import domain.krw.CurrencyTranslationProfit
import domain.krw.KrwIncomeTax
import domain.krw.KrwNetPayment
import domain.krw.KrwResidentTax
import domain.krw.KrwTotalPayment
import java.math.BigDecimal

@Suppress("JoinDeclarationAndAssignment")
sealed interface KrwTaxDetail {

    val krwTotalPayment: KrwTotalPayment
    val krwIncomeTax: KrwIncomeTax
    val krwResidentTax: KrwResidentTax
    val krwNetPayment: KrwNetPayment
    val currencyTranslationProfit: CurrencyTranslationProfit

    class PayeeKrwTaxDetail(
        foreignTaxDetail: ForeignTaxDetail.PayeeForeignTaxDetail,
        krwRoundedNeighboringCopyrightFee: BigDecimal,
    ) : KrwTaxDetail {

        override val krwTotalPayment: KrwTotalPayment
        override val krwIncomeTax: KrwIncomeTax
        override val krwResidentTax: KrwResidentTax
        override val krwNetPayment: KrwNetPayment
        override val currencyTranslationProfit: CurrencyTranslationProfit

        /**
         * Payee 유형은 외화 `총`지급액을 기초로 하는 원화 `총`지급액 및 세금을 바탕으로 원화 `순`지급액을 계산한다.
         */
        init {
            krwTotalPayment = KrwTotalPayment.payee(foreignTaxDetail.foreignTotalPayment, foreignTaxDetail.exchangeRate)

            krwIncomeTax = KrwIncomeTax.calculate(foreignTaxDetail.foreignIncomeTax, foreignTaxDetail.exchangeRate)

            krwResidentTax =
                KrwResidentTax.calculate(foreignTaxDetail.foreignResidentTax, foreignTaxDetail.exchangeRate)

            krwNetPayment = KrwNetPayment.payee(krwTotalPayment, krwIncomeTax, krwResidentTax)

            currencyTranslationProfit = CurrencyTranslationProfit.payee(krwTotalPayment, krwRoundedNeighboringCopyrightFee)
        }
    }

    class PayerKrwTaxDetail(
        foreignTaxDetail: ForeignTaxDetail.PayerForeignTaxDetail,
        krwRoundedNeighboringCopyrightFee: BigDecimal,
    ) : KrwTaxDetail {

        override val krwTotalPayment: KrwTotalPayment
        override val krwIncomeTax: KrwIncomeTax
        override val krwResidentTax: KrwResidentTax
        override val krwNetPayment: KrwNetPayment
        override val currencyTranslationProfit: CurrencyTranslationProfit

        /**
         * Payer 유형은 외화 `순`지급액을 기초로 하는 원화 `순`지급액 및 세금을 바탕으로 원화 `총`지급액을 계산한다.
         */
        init {
            krwNetPayment = KrwNetPayment.payer(foreignTaxDetail.foreignNetPayment, foreignTaxDetail.exchangeRate)

            krwIncomeTax = KrwIncomeTax.calculate(foreignTaxDetail.foreignIncomeTax, foreignTaxDetail.exchangeRate)

            krwResidentTax =
                KrwResidentTax.calculate(foreignTaxDetail.foreignResidentTax, foreignTaxDetail.exchangeRate)

            krwTotalPayment = KrwTotalPayment.payer(krwNetPayment, krwIncomeTax, krwResidentTax)

            currencyTranslationProfit = CurrencyTranslationProfit.payer(krwNetPayment, krwRoundedNeighboringCopyrightFee)
        }
    }
}
