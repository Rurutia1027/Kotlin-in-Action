package com.food.ordering.system.payment.service.domain.outbox.model

import java.math.BigDecimal
import java.time.ZonedDateTime

data class OrderEventPayload(
    val paymentId: String,
    val customerId: String,
    val orderId: String,
    val price: BigDecimal,
    val createdAt: ZonedDateTime,
    val paymentStatus: String,
    val failureMessages: List<String>
) {
    companion object {
        fun builder(): Builder = Builder()
    }

    class Builder {
        private var paymentId: String? = null
        private var customerId: String? = null
        private var orderId: String? = null
        private var price: BigDecimal? = null
        private var createdAt: ZonedDateTime? = null
        private var paymentStatus: String? = null
        private var failureMessages: List<String>? = null
        fun paymentId(value: String) = apply { this.paymentId = value }
        fun customerId(value: String) = apply { this.customerId = value }
        fun orderId(value: String) = apply { this.orderId = value }
        fun price(value: BigDecimal) = apply { this.price = value }
        fun createdAt(value: ZonedDateTime) = apply { this.createdAt = value }
        fun paymentStatus(value: String) = apply { this.paymentStatus = value }
        fun failureMessages(value: List<String>) = apply { this.failureMessages = value }
        fun build(): OrderEventPayload = OrderEventPayload(
            paymentId = paymentId!!,
            customerId = customerId!!,
            orderId = orderId!!,
            price = price!!,
            createdAt = createdAt!!,
            paymentStatus = paymentStatus!!,
            failureMessages = failureMessages!!
        )
    }
}