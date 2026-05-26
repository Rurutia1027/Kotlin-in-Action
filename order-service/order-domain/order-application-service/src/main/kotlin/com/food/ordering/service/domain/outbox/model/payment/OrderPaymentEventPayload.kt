package com.food.ordering.system.order.service.domain.outbox.model.payment

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal
import java.time.ZonedDateTime

data class OrderPaymentEventPayload(
    @JsonProperty val orderId: String? = null,
    @JsonProperty val customerId: String? = null,
    @JsonProperty val price: BigDecimal? = null,
    @JsonProperty val createdAt: ZonedDateTime? = null,
    @JsonProperty val paymentOrderStatus: String? = null,
) {
    class Builder {
        private var orderId: String? = null
        private var customerId: String? = null
        private var price: BigDecimal? = null
        private var createdAt: ZonedDateTime? = null
        private var paymentOrderStatus: String? = null

        fun orderId(value: String) = apply { orderId = value }
        fun customerId(value: String) = apply { customerId = value }
        fun price(value: BigDecimal) = apply { price = value }
        fun createdAt(value: ZonedDateTime) = apply { createdAt = value }
        fun paymentOrderStatus(value: String) = apply { paymentOrderStatus = value }

        fun build() = OrderPaymentEventPayload(
            orderId = orderId,
            customerId = customerId,
            price = price,
            createdAt = createdAt,
            paymentOrderStatus = paymentOrderStatus,
        )
    }

    companion object {
        @JvmStatic
        fun builder(): Builder = Builder()
    }
}
