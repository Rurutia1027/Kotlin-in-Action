package com.food.ordering.system.order.service.domain

import com.food.ordering.system.order.service.domain.dto.message.PaymentResponse
import com.food.ordering.system.order.service.domain.entity.Order.Companion.FAILURE_MESSAGE_DELIMITER
import com.food.ordering.system.order.service.domain.ports.input.message.listener.payment.PaymentResponseMessageListener
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.validation.annotation.Validated

@Validated
@Service
class PaymentResponseMessageListenerImpl(
    private val orderPaymentSaga: OrderPaymentSaga,
) : PaymentResponseMessageListener {

    private val log = LoggerFactory.getLogger(PaymentResponseMessageListenerImpl::class.java)

    override fun paymentCompleted(paymentResponse: PaymentResponse) {
        orderPaymentSaga.process(paymentResponse)
        log.info(
            "Order Payment Saga process operation is completed for order id: {}",
            paymentResponse.orderId,
        )
    }

    override fun paymentCancelled(paymentResponse: PaymentResponse) {
        orderPaymentSaga.rollback(paymentResponse)
        log.info(
            "Order is roll backed for order id: {} with failure messages: {}",
            paymentResponse.orderId,
            paymentResponse.failureMessages?.joinToString(FAILURE_MESSAGE_DELIMITER) ?: "",
        )
    }
}
