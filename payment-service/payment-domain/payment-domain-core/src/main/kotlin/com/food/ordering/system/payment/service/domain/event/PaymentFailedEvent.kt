package com.food.ordering.system.payment.service.domain.event

import com.food.ordering.system.payment.service.domain.entity.Payment
import java.time.ZonedDateTime

class PaymentFailedEvent(
    payment: Payment,
    createdAt: ZonedDateTime,
    failureMessages: List<String>,
) : PaymentEvent(payment, createdAt, failureMessages)