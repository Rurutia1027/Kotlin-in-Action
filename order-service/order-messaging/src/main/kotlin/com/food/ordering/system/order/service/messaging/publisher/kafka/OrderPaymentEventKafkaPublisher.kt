package com.food.ordering.system.order.service.messaging.publisher.kafka

import com.food.ordering.service.domain.config.OrderServiceConfigData
import com.food.ordering.system.kafka.order.avro.model.PaymentRequestAvroModel
import com.food.ordering.system.kafka.producer.KafkaMessageHelper
import com.food.ordering.system.kafka.producer.service.KafkaProducer
import com.food.ordering.system.order.service.domain.outbox.model.payment.OrderPaymentEventPayload
import com.food.ordering.system.order.service.domain.outbox.model.payment.OrderPaymentOutboxMessage
import com.food.ordering.system.order.service.domain.ports.output.message.publisher.payment.PaymentRequestMessagePublisher
import com.food.ordering.system.order.service.messaging.mapper.OrderMessagingDataMapper
import com.food.ordering.system.outbox.OutboxStatus
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.function.BiConsumer

@Component
class OrderPaymentEventKafkaPublisher(
    private val orderMessagingDataMapper: OrderMessagingDataMapper,
    private val kafkaProducer: KafkaProducer<String, PaymentRequestAvroModel>,
    private val orderServiceConfigData: OrderServiceConfigData,
    private val kafkaMessageHelper: KafkaMessageHelper,
) : PaymentRequestMessagePublisher {

    companion object {
        private val log = LoggerFactory.getLogger(OrderPaymentEventKafkaPublisher::class.java)
    }

    override fun publish(
        orderPaymentOutboxMessage: OrderPaymentOutboxMessage,
        outboxCallback: BiConsumer<OrderPaymentOutboxMessage, OutboxStatus>,
    ) {
        val orderPaymentEventPayload = kafkaMessageHelper.getOrderEventPayload(
            orderPaymentOutboxMessage.payload,
            OrderPaymentEventPayload::class.java,
        )
        val sagaId = orderPaymentOutboxMessage.sagaId.toString()

        log.info(
            "Received OrderPaymentOutboxMessage for order id: {} and saga id: {}",
            orderPaymentEventPayload.orderId,
            sagaId,
        )

        try {
            val paymentRequestAvroModel = orderMessagingDataMapper.orderPaymentEventToPaymentRequestAvroModel(
                sagaId,
                orderPaymentEventPayload,
            )

            kafkaProducer.send(
                orderServiceConfigData.paymentRequestTopicName!!,
                sagaId,
                paymentRequestAvroModel,
                kafkaMessageHelper.getKafkaCallback(
                    orderServiceConfigData.paymentRequestTopicName!!,
                    paymentRequestAvroModel,
                    orderPaymentOutboxMessage,
                    outboxCallback,
                    orderPaymentEventPayload.orderId!!,
                    "PaymentRequestAvroModel",
                ),
            )

            log.info(
                "OrderPaymentEventPayload sent to Kafka for order id: {} and saga id: {}",
                orderPaymentEventPayload.orderId,
                sagaId,
            )
        } catch (e: Exception) {
            log.error(
                "Error while sending OrderPaymentEventPayload to kafka with order id: {} and saga id: {}, error: {}",
                orderPaymentEventPayload.orderId,
                sagaId,
                e.message,
            )
        }
    }
}
