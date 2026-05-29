package com.food.ordering.system.order.service.dataaccess.outbox.payment.mapper

import com.food.ordering.system.order.service.dataaccess.outbox.payment.entity.PaymentOutboxEntity
import com.food.ordering.system.order.service.domain.outbox.model.payment.OrderPaymentOutboxMessage
import org.springframework.stereotype.Component

@Component
class PaymentOutboxDataAccessMapper {

    fun orderPaymentOutboxMessageToOutboxEntity(
        orderPaymentOutboxMessage: OrderPaymentOutboxMessage,
    ): PaymentOutboxEntity =
        PaymentOutboxEntity(
            id = orderPaymentOutboxMessage.id,
            sagaId = orderPaymentOutboxMessage.sagaId,
            createdAt = orderPaymentOutboxMessage.createdAt,
            processedAt = orderPaymentOutboxMessage.processedAt,
            type = orderPaymentOutboxMessage.type,
            payload = orderPaymentOutboxMessage.payload,
            sagaStatus = orderPaymentOutboxMessage.sagaStatus,
            orderStatus = orderPaymentOutboxMessage.orderStatus,
            outboxStatus = orderPaymentOutboxMessage.outboxStatus,
            version = orderPaymentOutboxMessage.version,
        )

    fun paymentOutboxEntityToOrderPaymentOutboxMessage(
        paymentOutboxEntity: PaymentOutboxEntity,
    ): OrderPaymentOutboxMessage =
        OrderPaymentOutboxMessage(
            id = paymentOutboxEntity.id!!,
            sagaId = paymentOutboxEntity.sagaId!!,
            createdAt = paymentOutboxEntity.createdAt!!,
            processedAt = paymentOutboxEntity.processedAt,
            type = paymentOutboxEntity.type!!,
            payload = paymentOutboxEntity.payload!!,
            sagaStatus = paymentOutboxEntity.sagaStatus!!,
            orderStatus = paymentOutboxEntity.orderStatus!!,
            outboxStatus = paymentOutboxEntity.outboxStatus!!,
            version = paymentOutboxEntity.version,
        )
}
