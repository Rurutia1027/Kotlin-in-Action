package com.food.ordering.system.domain.valueobject

import java.math.BigDecimal
import java.math.RoundingMode

class Money(val amount: BigDecimal) {
    fun isGreaterThanZero(): Boolean =
        amount.compareTo(BigDecimal.ZERO) > 0

    fun isGreaterThan(money: Money): Boolean =
        amount.compareTo(money.amount) > 0

    fun add(money: Money): Money =
        Money(setScale(amount.add(money.amount)))

    fun subtract(money: Money): Money =
        Money(setScale(amount.subtract(money.amount)))

    fun multiply(multiplier: Int): Money =
        Money(setScale(amount.multiply(BigDecimal(multiplier))))

    override fun equals(other: Any?): Boolean {
        if (this == other) return true
        if (other == null || javaClass != other.javaClass) return false
        val money = other as Money
        return amount == money.amount
    }

    override fun hashCode(): Int = amount.hashCode()

    private fun setScale(input: BigDecimal): BigDecimal =
        input.setScale(2, RoundingMode.HALF_EVEN)

    companion object {
        @JvmField
        val ZERO: Money = Money(BigDecimal.ZERO)
    }
}