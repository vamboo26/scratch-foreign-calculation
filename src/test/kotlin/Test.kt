import org.junit.jupiter.api.Test
import java.math.BigDecimal
import kotlin.test.assertEquals

class Test {

    @Test
    fun test() {

        val input = input()
        val actualResults = input.map {
            //FIXME
            MyCalculator.calculate(it)
        }.toList()

        actualResults.forEachIndexed { i, result ->
            println("ACTUAL   = ${result}")
            println("EXPECTED = ${expectedOutput()[i]}")
            assertEquals(expectedOutput()[i], result)
        }
    }

    private fun input(): List<Input> {

        return Test::class.java.getResourceAsStream("/input.tsv")?.bufferedReader()?.useLines { lines ->
            lines.map { line ->
                val values = line.split("\t")
                Input(
                    settlementMonth = values[0],
                    rightHolderName = values[1],
                    neighboringCopyrightFee = BigDecimal(values[2]),
                    currency = values[3],
                    exchangeRate = BigDecimal(values[4]),
                    nation = values[5],
                    withholdingTaxBurdenType = values[6],
                    withholdingTaxRate = BigDecimal(values[7])
                )
            }.toList()
                .sortedBy { it.rightHolderName }
        }
            ?: throw IllegalStateException("Cannot find resource: /input.tsv")
    }

    private fun expectedOutput(): List<Output> {

        return Test::class.java.getResourceAsStream("/output.tsv")?.bufferedReader()?.useLines { lines ->
            lines.map { line ->
                val values = line.split("\t")
                Output(
                    rightHolderName = values[0],
                    neighboringCopyrightFee = BigDecimal(values[1]),
                    currency = values[2],
                    exchangeRate = BigDecimal(values[3]),
                    nation = values[4],
                    withholdingTaxBurdenType = values[5],
                    withholdingTaxRate = BigDecimal(values[6]),
                    foreignTotalPayment = BigDecimal(values[7]),
                    foreignIncomeTax = BigDecimal(values[8]),
                    foreignResidentTax = BigDecimal(values[9]),
                    foreignNetPayment = BigDecimal(values[10]),
                    krwRoundedNeighboringCopyrightFee = BigDecimal(values[11]),
                    currencyTranslationProfit = values[12].toInt(),
                    krwTotalPayment = BigDecimal(values[13]),
                    krwIncomeTax = BigDecimal(values[14]),
                    krwResidentTax = BigDecimal(values[15]),
                    krwNetPayment = BigDecimal(values[16]),
                    settlementMonth = values[17]
                )
            }.toList()
                .sortedBy { it.rightHolderName }
        }
            ?: throw IllegalStateException("Cannot find resource: /output.tsv")
    }
}
