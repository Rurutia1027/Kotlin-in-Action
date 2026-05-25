package com.food.ordering.system.payment.service.domain.ports.inputs.message.listener

import com.food.ordering.system.payment.service.domain.dto.PaymentRequest

interface PaymentRequestMessageListener {
    fun completePayment(paymentRequest: PaymentRequest): Unit
    fun cancelPayment(paymentRequest: PaymentRequest): Unit
}