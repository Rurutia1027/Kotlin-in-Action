package com.food.ordering.system.order.service.messaging.listener.kafka

import com.food.ordering.system.kafka.consumer.KafkaConsumer
import com.food.ordering.system.kafka.order.avro.model.OrderApprovalStatus
import com.food.ordering.system.kafka.order.avro.model.RestaurantApprovalResponseAvroModel
import com.food.ordering.system.order.service.domain.entity.Order
import com.food.ordering.system.order.service.domain.exception.OrderNotFoundException
import com.food.ordering.system.order.service.domain.ports.input.message.listener.restaurantapproval.RestaurantApprovalResponseMessageListener
import com.food.ordering.system.order.service.messaging.mapper.OrderMessagingDataMapper
import org.slf4j.LoggerFactory
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class RestaurantApprovalResponseKafkaListener(
    private val restaurantApprovalResponseMessageListener: RestaurantApprovalResponseMessageListener,
    private val orderMessagingDataMapper: OrderMessagingDataMapper
) : KafkaConsumer<RestaurantApprovalResponseAvroModel> {

    private val log = LoggerFactory.getLogger(RestaurantApprovalResponseKafkaListener::class.java)

    @KafkaListener(
        id = "\${kafka-consumer-config.restaurant-approval-consumer-group-id}",
        topics = ["\${order-service.restaurant-approval-response-topic-name}"]
    )
    override fun receive(
        messages: List<RestaurantApprovalResponseAvroModel>,
        keys: List<String>,
        partitions: List<Int>,
        offsets: List<Long>
    ) {
        log.info(
            "{} number of restaurant approval responses received with keys {}, partitions {} and offsets {}",
            messages.size,
            keys,
            partitions,
            offsets,
        )

        messages.forEach { restaurantApprovalResponseAvroModel ->
            try {
                when (restaurantApprovalResponseAvroModel.orderApprovalStatus) {
                    OrderApprovalStatus.APPROVED -> {
                        log.info(
                            "Processing approved order for order id: {}",
                            restaurantApprovalResponseAvroModel.orderId,
                        )
                        restaurantApprovalResponseMessageListener.orderApproved(
                            orderMessagingDataMapper.approvalResponseAvroModelToApprovalResponse(
                                restaurantApprovalResponseAvroModel,
                            ),
                        )
                    }

                    OrderApprovalStatus.REJECTED -> {
                        log.info(
                            "Processing rejected order for order id: {}, with failure messages: {}",
                            restaurantApprovalResponseAvroModel.orderId,
                            restaurantApprovalResponseAvroModel.failureMessages.joinToString(
                                Order.FAILURE_MESSAGE_DELIMITER,
                            ),
                        )
                        restaurantApprovalResponseMessageListener.orderRejected(
                            orderMessagingDataMapper.approvalResponseAvroModelToApprovalResponse(
                                restaurantApprovalResponseAvroModel,
                            ),
                        )
                    }

                    else -> Unit
                }
            } catch (_: OptimisticLockingFailureException) {
                log.error(
                    "Caught optimistic locking exception in RestaurantApprovalResponseKafkaListener for order id: {}",
                    restaurantApprovalResponseAvroModel.orderId,
                )
            } catch (_: OrderNotFoundException) {
                log.error(
                    "No order found for order id: {}",
                    restaurantApprovalResponseAvroModel.orderId,
                )
            }
        }
    }
}