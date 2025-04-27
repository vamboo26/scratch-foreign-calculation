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
