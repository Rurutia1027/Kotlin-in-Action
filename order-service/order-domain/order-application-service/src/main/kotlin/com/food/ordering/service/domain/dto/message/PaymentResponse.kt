package com.food.ordering.system.order.service.domain.dto.message

import com.food.ordering.system.domain.valueobject.PaymentStatus
import java.math.BigDecimal
import java.time.Instant

data class PaymentResponse(
    val id: String? = null,
    val sagaId: String? = null,
    val orderId: String? = null,
    val paymentId: String? = null,
    val customerId: String? = null,
    val price: BigDecimal? = null,
    val createdAt: Instant? = null,
    val paymentStatus: PaymentStatus? = null,
    val failureMessages: List<String>? = null,
)
