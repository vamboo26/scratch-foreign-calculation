import java.math.BigDecimal

data class Input(
    val settlementMonth: String,
    val rightHolderName: String,
    // 총 발생한 인접권료 (원화 기준)
    val neighboringCopyrightFee: BigDecimal,
    // 통화
    val currency: String,
    // 환율 ex) 1315.20
    val exchangeRate: BigDecimal,
    // 국가 ex) JPN
    val nation: String,
    // 원천세부담유형 PAYEE(수취자부담), PAYER(납부자부담) 중 하나, 이 유형에 따라 금액 계산식이 달라진다.
    val withholdingTaxBurdenType: String,
    // 원천세율 ex) 0.11
    val withholdingTaxRate: BigDecimal,
)

data class Output(
    val settlementMonth: String,
    val rightHolderName: String,
    val neighboringCopyrightFee: BigDecimal,
    val currency: String,
    val exchangeRate: BigDecimal,
    val nation: String,
    val withholdingTaxBurdenType: String,
    val withholdingTaxRate: BigDecimal,
    // 외화 총지급액
    val foreignTotalPayment: BigDecimal,
    // 외화 소득세
    val foreignIncomeTax: BigDecimal,
    // 외화 주민세
    val foreignResidentTax: BigDecimal,
    // 외화 순지급액
    val foreignNetPayment: BigDecimal,
    // 통화환산이익
    val currencyTranslationProfit: Int,
    // 원화 반올림 인접권료
    val krwRoundedNeighboringCopyrightFee: BigDecimal,
    // 원화 총지급액
    val krwTotalPayment: BigDecimal,
    // 원화 소득세
    val krwIncomeTax: BigDecimal,
    // 원화 주민세
    val krwResidentTax: BigDecimal,
    // 원화 순지급액
    val krwNetPayment: BigDecimal,
)

interface Calculator {
    fun calculate(summary: Input): Output
}

enum class WithholdingTaxBurdenType {
    PAYEE,
    PAYER,
    ;

    companion object {
        fun from(value: String): WithholdingTaxBurdenType {
            return entries.firstOrNull { it.name.equals(value, ignoreCase = true) }
                ?: throw IllegalArgumentException("Invalid withholding tax burden type: $value")
        }
    }
}

object MyCalculator : Calculator {

    override fun calculate(summary: Input): Output {

        val taxBurdenType = WithholdingTaxBurdenType.from(summary.withholdingTaxBurdenType)

        return when (taxBurdenType) {
            WithholdingTaxBurdenType.PAYEE -> TaxCalculator.PayeeTaxCalculator.calculate(summary)
            WithholdingTaxBurdenType.PAYER -> TaxCalculator.PayerTaxCalculator.calculate(summary)
        }
    }
}
