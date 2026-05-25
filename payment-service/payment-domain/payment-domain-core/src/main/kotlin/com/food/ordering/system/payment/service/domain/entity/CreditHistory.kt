package com.food.ordering.system.payment.service.domain.entity

import com.food.ordering.system.domain.entity.BaseEntity
import com.food.ordering.system.domain.valueobject.CustomerId
import com.food.ordering.system.domain.valueobject.Money
import com.food.ordering.system.payment.service.domain.valueobject.CreditHistoryId
import com.food.ordering.system.payment.service.domain.valueobject.TransactionType

class CreditHistory private constructor(
    val customerId: CustomerId,
    val amount: Money,
    val transactionType: TransactionType,
) : BaseEntity<CreditHistoryId>() {
    class Builder {
        private var creditHistoryId: CreditHistoryId? = null
        private var customerId: CustomerId? = null
        private var amount: Money? = null
        private var transactionType: TransactionType? = null

        fun creditHistoryId(value: CreditHistoryId) = apply { creditHistoryId = value }
        fun customerId(value: CustomerId) = apply { customerId = value }
        fun amount(value: Money) = apply { amount = value }
        fun transactionType(value: TransactionType) = apply { transactionType = value }

        fun build(): CreditHistory =
            CreditHistory(
                customerId = customerId!!,
                amount = amount!!,
                transactionType = transactionType!!,
            ).also { it.id = creditHistoryId }
    }

    companion object {
        @JvmStatic
        fun builder(): Builder = Builder()
    }
}
