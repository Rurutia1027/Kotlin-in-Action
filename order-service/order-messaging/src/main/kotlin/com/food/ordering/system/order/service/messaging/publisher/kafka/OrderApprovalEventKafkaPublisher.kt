package com.food.ordering.system.order.service.messaging.publisher.kafka

import com.food.ordering.service.domain.config.OrderServiceConfigData
import com.food.ordering.system.kafka.order.avro.model.RestaurantApprovalRequestAvroModel
import com.food.ordering.system.kafka.producer.KafkaMessageHelper
import com.food.ordering.system.kafka.producer.service.KafkaProducer
import com.food.ordering.system.order.service.domain.outbox.model.approval.OrderApprovalEventPayload
import com.food.ordering.system.order.service.domain.outbox.model.approval.OrderApprovalOutboxMessage
import com.food.ordering.system.order.service.domain.ports.output.message.publisher.restaurantapproval.RestaurantApprovalRequestMessagePublisher
import com.food.ordering.system.order.service.messaging.mapper.OrderMessagingDataMapper
import com.food.ordering.system.outbox.OutboxStatus
import org.slf4j.LoggerFactory
import org.springframework.kafka.support.SendResult
import org.springframework.stereotype.Component
import java.util.concurrent.CompletableFuture
import java.util.function.BiConsumer

@Component
class OrderApprovalEventKafkaPublisher(
    private val orderMessagingDataMapper: OrderMessagingDataMapper,
    private val kafkaProducer: KafkaProducer<String, RestaurantApprovalRequestAvroModel>,
    private val orderServiceConfigData: OrderServiceConfigData,
    private val kafkaMessageHelper: KafkaMessageHelper,
) : RestaurantApprovalRequestMessagePublisher {

    companion object {
        private val log = LoggerFactory.getLogger(OrderApprovalEventKafkaPublisher::class.java)
    }

    override fun publish(
        orderApprovalOutboxMessage: OrderApprovalOutboxMessage,
        outboxCallback: BiConsumer<OrderApprovalOutboxMessage, OutboxStatus>,
    ) {
        val orderApprovalEventPayload = kafkaMessageHelper.getOrderEventPayload(
            orderApprovalOutboxMessage.payload,
            OrderApprovalEventPayload::class.java,
        )

        val sagaId = orderApprovalOutboxMessage.sagaId.toString()

        log.info(
            "Received OrderApprovalOutboxMessage for order id: {} and saga id: {}",
            orderApprovalEventPayload.orderId,
            sagaId,
        )

        try {
            val restaurantApprovalRequestAvroModel = orderMessagingDataMapper
                .orderApprovalEventToRestaurantApprovalRequestAvroModel(sagaId, orderApprovalEventPayload)

            val future: CompletableFuture<SendResult<String, RestaurantApprovalRequestAvroModel>> =
                kafkaProducer.send(
                    orderServiceConfigData.restaurantApprovalRequestTopicName!!,
                    sagaId,
                    restaurantApprovalRequestAvroModel,
                )

            future.whenComplete { _, ex ->
                kafkaMessageHelper.handleKafkaCallback(
                    ex,
                    orderServiceConfigData.restaurantApprovalRequestTopicName!!,
                    restaurantApprovalRequestAvroModel,
                    orderApprovalOutboxMessage,
                    outboxCallback,
                    orderApprovalEventPayload.orderId!!,
                    "RestaurantApprovalRequestAvroModel",
                )
            }

            log.info(
                "OrderApprovalEventPayload sent to kafka for order id: {} and saga id: {}",
                restaurantApprovalRequestAvroModel.orderId,
                sagaId,
            )
        } catch (e: Exception) {
            log.error(
                "Error while sending OrderApprovalEventPayload to kafka for order id: {} and saga id: {}, error: {}",
                orderApprovalEventPayload.orderId,
                sagaId,
                e.message,
            )
        }
    }
}
