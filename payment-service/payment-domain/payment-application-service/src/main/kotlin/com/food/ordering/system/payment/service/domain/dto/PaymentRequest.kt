package com.food.ordering.system.payment.service.domain.dto

import com.food.ordering.system.domain.valueobject.PaymentOrderStatus
import java.math.BigDecimal
import java.time.Instant

data class PaymentRequest(
    val id: String,
    val sagaId: String,
    val orderId: String,
    val customerId: String,
    val price: BigDecimal,
    val createdAt: Instant,
    var paymentOrderStatus: PaymentOrderStatus,
) {
    companion object {
        fun builder(): Builder = Builder()
    }

    class Builder {
        private var id: String? = null
        private var sagaId: String? = null
        private var orderId: String? = null
        private var customerId: String? = null
        private var price: BigDecimal? = null
        private var createdAt: Instant? = null
        private var paymentOrderStatus: PaymentOrderStatus? = null
        fun id(value: String) = apply { this.id = value }
        fun sagaId(value: String) = apply { this.sagaId = value }
        fun orderId(value: String) = apply { this.orderId = value }
        fun customerId(value: String) = apply { this.customerId = value }
        fun price(value: BigDecimal) = apply { this.price = value }
        fun createdAt(value: Instant) = apply { this.createdAt = value }
        fun paymentOrderStatus(value: PaymentOrderStatus) = apply { this.paymentOrderStatus = value }
        fun build(): PaymentRequest = PaymentRequest(
            id = id!!,
            sagaId = sagaId!!,
            orderId = orderId!!,
            customerId = customerId!!,
            price = price!!,
            createdAt = createdAt!!,
            paymentOrderStatus = paymentOrderStatus!!
        )
    }
}