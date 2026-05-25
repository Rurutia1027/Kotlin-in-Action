package com.food.ordering.system.payment.service.domain.entity

import com.food.ordering.system.domain.entity.BaseEntity
import com.food.ordering.system.domain.valueobject.CustomerId
import com.food.ordering.system.domain.valueobject.Money
import com.food.ordering.system.payment.service.domain.valueobject.CreditEntryId

class CreditEntry private constructor(
    val customerId: CustomerId,
    totalCreditAmount: Money,
) : BaseEntity<CreditEntryId>() {
    var totalCreditAmount: Money = totalCreditAmount
    fun addCreditAmount(amount: Money) {
        totalCreditAmount = totalCreditAmount.add(amount)
    }

    fun subtractCreditAmount(amount: Money) {
        totalCreditAmount = totalCreditAmount.subtract(amount)
    }

    class Builder {
        private var creditEntryId: CreditEntryId? = null
        private var customerId: CustomerId? = null
        private var totalCreditAmount: Money? = null

        fun creditEntryId(value: CreditEntryId) = apply { creditEntryId = value }
        fun customerId(value: CustomerId) = apply { customerId = value }
        fun totalCreditAmount(value: Money) = apply { totalCreditAmount = value }

        fun build(): CreditEntry =
            CreditEntry(
                customerId = customerId!!,
                totalCreditAmount = totalCreditAmount!!,
            ).also { it.id = creditEntryId }
    }

    companion object {
        @JvmStatic
        fun builder(): Builder = Builder()
    }
}