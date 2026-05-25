package com.food.ordering.system.payment.service.domain

import com.food.ordering.system.payment.service.domain.dto.PaymentRequest
import com.food.ordering.system.payment.service.domain.ports.inputs.message.listener.PaymentRequestMessageListener
import org.springframework.stereotype.Service

@Service
class PaymentRequestMessageListenerImpl(
    private val paymentRequestHelper: PaymentRequestHelper
) : PaymentRequestMessageListener {

    override fun completePayment(paymentRequest: PaymentRequest) {
        paymentRequestHelper.persistPayment(paymentRequest)
    }

    override fun cancelPayment(paymentRequest: PaymentRequest) {
        paymentRequestHelper.persistCancelPayment(paymentRequest)
    }
}