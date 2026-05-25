package com.food.ordering.system.payment.service.messaging.publisher.kafka

import OrderOutboxMessage
import com.food.ordering.system.kafka.order.avro.model.PaymentResponseAvroModel
import com.food.ordering.system.kafka.producer.KafkaMessageHelper
import com.food.ordering.system.kafka.producer.service.KafkaProducer
import com.food.ordering.system.outbox.OutboxStatus
import com.food.ordering.system.payment.service.domain.config.PaymentServiceConfigData
import com.food.ordering.system.payment.service.domain.outbox.model.OrderEventPayload
import com.food.ordering.system.payment.service.domain.ports.outputs.message.publisher.PaymentResponseMessagePublisher
import com.food.ordering.system.payment.service.messaging.mapper.PaymentMessagingDataMapper
import org.slf4j.LoggerFactory
import org.springframework.kafka.support.SendResult
import org.springframework.stereotype.Component
import java.util.concurrent.CompletableFuture
import java.util.function.BiConsumer

@Component
class PaymentEventKafkaPublisher(
    private val paymentMessagingDataMapper: PaymentMessagingDataMapper,
    private val kafkaProducer: KafkaProducer<String, PaymentResponseAvroModel>,
    private val paymentServiceConfigData: PaymentServiceConfigData,
    private val kafkaMessageHelper: KafkaMessageHelper
) : PaymentResponseMessagePublisher {
    companion object {
        private val log = LoggerFactory.getLogger(PaymentEventKafkaPublisher::class.java)
    }

    override fun publish(
        orderOutboxMessage: OrderOutboxMessage,
        outboxCallback: BiConsumer<OrderOutboxMessage, OutboxStatus>
    ) {

        val orderEventPayload = kafkaMessageHelper.getOrderEventPayload(
            orderOutboxMessage.payload,
            OrderEventPayload::class.java
        )

        val sagaId = orderOutboxMessage.sagaId.toString()
        log.info(
            "Received OrderOutboxMessage for order id: {} and saga id: {}",
            orderEventPayload.orderId, sagaId
        )
        try {
            val avroModel = paymentMessagingDataMapper
                .orderEventPayloadToPaymentResponseAvroModel(sagaId, orderEventPayload)
            val future: CompletableFuture<SendResult<String, PaymentResponseAvroModel>> = kafkaProducer.send(
                paymentServiceConfigData.paymentResponseTopicName,
                sagaId,
                avroModel
            )

            future.whenComplete { _, ex ->
                kafkaMessageHelper.handleKafkaCallback(
                    ex,
                    paymentServiceConfigData.paymentResponseTopicName,
                    avroModel,
                    orderOutboxMessage,
                    outboxCallback,
                    orderEventPayload.orderId,
                    "PaymentResponseAvroModel",
                )
            }

            log.info(
                "PaymentResponseAvroModel sent to kafka for order id: {} and saga id: {}",
                avroModel.orderId,
                sagaId
            )
        } catch (e: Exception) {
            log.error(
                "Error while sending PaymentResponseAvroModel to kafka with order id: {} and saga id: {}, error: {}",
                orderEventPayload.orderId,
                sagaId,
                e.message
            )
        }
    }
}