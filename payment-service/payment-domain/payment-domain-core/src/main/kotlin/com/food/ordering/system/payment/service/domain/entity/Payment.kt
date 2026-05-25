package com.food.ordering.system.payment.service.domain.entity

import com.food.ordering.system.domain.constants.DomainConstants.UTC
import com.food.ordering.system.domain.entity.AggregateRoot
import com.food.ordering.system.domain.valueobject.CustomerId
import com.food.ordering.system.domain.valueobject.Money
import com.food.ordering.system.domain.valueobject.OrderId
import com.food.ordering.system.domain.valueobject.PaymentStatus
import com.food.ordering.system.payment.service.domain.valueobject.PaymentId
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*

class Payment private constructor(
    val orderId: OrderId,
    val customerId: CustomerId,
    val price: Money
) : AggregateRoot<PaymentId>() {

    var paymentStatus: PaymentStatus? = null
        private set
    var createdAt: ZonedDateTime? = null
        private set

    fun initializePayment() {
        id = PaymentId(UUID.randomUUID())
        createdAt = ZonedDateTime.now(ZoneId.of(UTC))
    }

    fun validatePayment(failureMessages: MutableList<String>) {
        if (!price.isGreaterThanZero()) {
            failureMessages.add("Total price must be greater than zero!")
        }
    }

    fun updateStatus(status: PaymentStatus) {
        paymentStatus = status
    }

    class Builder {
        private var paymentId: PaymentId? = null
        private var orderId: OrderId? = null
        private var customerId: CustomerId? = null
        private var price: Money? = null
        private var paymentStatus: PaymentStatus? = null
        private var createdAt: ZonedDateTime? = null

        fun paymentId(value: PaymentId) = apply { paymentId = value }
        fun orderId(value: OrderId) = apply { orderId = value }
        fun customerId(value: CustomerId) = apply { customerId = value }
        fun price(value: Money) = apply { price = value }
        fun paymentStatus(value: PaymentStatus) = apply { paymentStatus = value }
        fun createdAt(value: ZonedDateTime) = apply { createdAt = value }

        fun build(): Payment =
            Payment(
                orderId = orderId!!,
                customerId = customerId!!,
                price = price!!,
            ).also {
                it.id = paymentId
                it.paymentStatus = paymentStatus
                it.createdAt = createdAt
            }
    }

    companion object {
        @JvmStatic
        fun builder(): Builder = Builder()
    }
}