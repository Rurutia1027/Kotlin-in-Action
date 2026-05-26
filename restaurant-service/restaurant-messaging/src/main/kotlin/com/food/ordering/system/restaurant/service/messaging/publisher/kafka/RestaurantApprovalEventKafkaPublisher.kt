package com.food.ordering.system.restaurant.service.messaging.publisher.kafka

import com.food.ordering.system.kafka.order.avro.model.RestaurantApprovalResponseAvroModel
import com.food.ordering.system.kafka.producer.KafkaMessageHelper
import com.food.ordering.system.kafka.producer.service.KafkaProducer
import com.food.ordering.system.outbox.OutboxStatus
import com.food.ordering.system.restaurant.service.domain.config.RestaurantServiceConfigData
import com.food.ordering.system.restaurant.service.domain.outbox.model.OrderEventPayload
import com.food.ordering.system.restaurant.service.domain.outbox.model.OrderOutboxMessage
import com.food.ordering.system.restaurant.service.domain.ports.outputs.message.publisher.RestaurantApprovalResponseMessagePublisher
import com.food.ordering.system.restaurant.service.messaging.mapper.RestaurantMessagingDataMapper
import org.slf4j.LoggerFactory
import org.springframework.kafka.support.SendResult
import org.springframework.stereotype.Component
import java.util.concurrent.CompletableFuture
import java.util.function.BiConsumer

@Component
class RestaurantApprovalEventKafkaPublisher(
    private val restaurantMessagingDataMapper: RestaurantMessagingDataMapper,
    private val kafkaProducer: KafkaProducer<String, RestaurantApprovalResponseAvroModel>,
    private val restaurantServiceConfigData: RestaurantServiceConfigData,
    private val kafkaMessageHelper: KafkaMessageHelper
) : RestaurantApprovalResponseMessagePublisher {

    companion object {
        private val log = LoggerFactory.getLogger(RestaurantApprovalEventKafkaPublisher::class.java)
    }

    override fun publish(
        orderOutboxMessage: OrderOutboxMessage,
        outboxCallback: BiConsumer<OrderOutboxMessage, OutboxStatus>
    ) {
        val orderEventPayload = kafkaMessageHelper.getOrderEventPayload(
            orderOutboxMessage.payload,
            OrderEventPayload::class.java,
        )

        val sagaId = orderOutboxMessage.sagaId.toString()
        log.info("Received OrderOutboxMessage for order id: {} and saga id: {}", orderEventPayload.orderId, sagaId)

        try {
            val avroModel = restaurantMessagingDataMapper
                .orderEventPayloadToRestaurantApprovalResponseAvroModel(sagaId, orderEventPayload)
            val future: CompletableFuture<SendResult<String, RestaurantApprovalResponseAvroModel>> = kafkaProducer.send(
                restaurantServiceConfigData.restaurantApprovalResponseTopicName,
                sagaId,
                avroModel
            )

            future.whenComplete { _, ex ->
                kafkaMessageHelper.handleKafkaCallback(
                    ex,
                    restaurantServiceConfigData.restaurantApprovalResponseTopicName,
                    avroModel,
                    orderOutboxMessage,
                    outboxCallback,
                    orderEventPayload.orderId,
                    "RestaurantApprovalResponseAvroModel"
                )
            }
            log.info(
                "RestaurantApprovalResponseAvroModel sent to kafka for order id: {} and saga id: {}",
                avroModel.orderId,
                sagaId
            )
        } catch (e: Exception) {
            log.error(
                "Error while sending RestaurantApprovalResponseAvroModel to kafka with order id: {} and saga id: {}, error: {}",
                orderEventPayload.orderId,
                sagaId,
                e.message
            )
        }
    }
}