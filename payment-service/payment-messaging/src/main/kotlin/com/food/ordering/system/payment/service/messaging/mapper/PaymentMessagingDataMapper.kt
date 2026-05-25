package com.food.ordering.system.payment.service.messaging.mapper

import com.food.ordering.system.domain.valueobject.PaymentOrderStatus
import com.food.ordering.system.kafka.order.avro.model.PaymentRequestAvroModel
import com.food.ordering.system.kafka.order.avro.model.PaymentResponseAvroModel
import com.food.ordering.system.kafka.order.avro.model.PaymentStatus
import com.food.ordering.system.payment.service.domain.dto.PaymentRequest
import com.food.ordering.system.payment.service.domain.outbox.model.OrderEventPayload
import org.springframework.stereotype.Component
import java.util.*

@Component
class PaymentMessagingDataMapper {
    fun paymentRequestAvroModelToPaymentRequest(
        paymentRequestAvroModel: PaymentRequestAvroModel
    ): PaymentRequest =
        PaymentRequest.builder()
            .id(paymentRequestAvroModel.id.toString())
            .sagaId(paymentRequestAvroModel.sagaId.toString())
            .customerId(paymentRequestAvroModel.customerId.toString())
            .orderId(paymentRequestAvroModel.orderId.toString())
            .price(paymentRequestAvroModel.price)
            .createdAt(paymentRequestAvroModel.createdAt)
            .paymentOrderStatus(
                PaymentOrderStatus.valueOf(paymentRequestAvroModel.paymentOrderStatus.name),
            )
            .build()

    fun orderEventPayloadToPaymentResponseAvroModel(
        sagaId: String,
        orderEventPayload: OrderEventPayload
    ): PaymentResponseAvroModel =
        PaymentResponseAvroModel.newBuilder()
            .setId(UUID.randomUUID())
            .setSagaId(UUID.fromString(sagaId))
            .setPaymentId(UUID.fromString(orderEventPayload.paymentId))
            .setCustomerId(UUID.fromString(orderEventPayload.customerId))
            .setOrderId(UUID.fromString(orderEventPayload.orderId))
            .setPrice(orderEventPayload.price)
            .setCreatedAt(orderEventPayload.createdAt.toInstant())
            .setPaymentStatus(PaymentStatus.valueOf(orderEventPayload.paymentStatus))
            .setFailureMessages(orderEventPayload.failureMessages)
            .build()
}