import java.math.BigDecimal
import java.math.RoundingMode

fun BigDecimal.divideWithScale(other: BigDecimal, scale: Int): BigDecimal {
    return this.divide(other, scale, RoundingMode.HALF_UP)
}

fun BigDecimal.multiplyWithScale(other: BigDecimal, scale: Int): BigDecimal {
    return this.multiply(other).setScale(scale, RoundingMode.HALF_UP)
}
