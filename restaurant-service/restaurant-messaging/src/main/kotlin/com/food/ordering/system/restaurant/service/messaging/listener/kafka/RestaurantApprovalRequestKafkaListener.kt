package com.food.ordering.system.restaurant.service.messaging.listener.kafka

import com.food.ordering.system.kafka.consumer.KafkaConsumer
import com.food.ordering.system.kafka.order.avro.model.RestaurantApprovalRequestAvroModel
import com.food.ordering.system.restaurant.service.domain.exception.RestaurantApplicationServiceException
import com.food.ordering.system.restaurant.service.domain.exception.RestaurantNotFoundException
import com.food.ordering.system.restaurant.service.domain.ports.inputs.message.listener.RestaurantApprovalRequestMessageListener
import com.food.ordering.system.restaurant.service.messaging.mapper.RestaurantMessagingDataMapper
import org.postgresql.util.PSQLState
import org.slf4j.LoggerFactory
import org.springframework.dao.DataAccessException
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component
import java.sql.SQLException

@Component
class RestaurantApprovalRequestKafkaListener(
    private val restaurantApprovalRequestMessageListener: RestaurantApprovalRequestMessageListener,
    private val restaurantMessagingDataMapper: RestaurantMessagingDataMapper,
) : KafkaConsumer<RestaurantApprovalRequestAvroModel> {

    private val log = LoggerFactory.getLogger(RestaurantApprovalRequestKafkaListener::class.java)

    @KafkaListener(
        id = "\${kafka-consumer-config.restaurant-approval-consumer-group-id}",
        topics = ["\${restaurant-service.restaurant-approval-request-topic-name}"],
    )
    override fun receive(
        @Payload messages: List<RestaurantApprovalRequestAvroModel>,
        @Header(KafkaHeaders.RECEIVED_KEY) keys: List<String>,
        @Header(KafkaHeaders.RECEIVED_PARTITION) partitions: List<Int>,
        @Header(KafkaHeaders.OFFSET) offsets: List<Long>
    ) {
        log.info(
            "Processing order approval for order id: {}",
            messages.size,
            keys,
            partitions,
            offsets
        )

        messages.forEach { restaurantApprovalRequestAvroModel ->
            try {
                log.info(
                    "Processing order approval for order id: {}",
                    restaurantApprovalRequestAvroModel.orderId
                )

                restaurantApprovalRequestMessageListener.approvalOrder(
                    restaurantMessagingDataMapper.restaurantApprovalRequestAvroModelToRestaurantApproval(
                        restaurantApprovalRequestAvroModel,
                    )
                )
            } catch (e: DataAccessException) {
                val sqlException = e.rootCause as? SQLException
                if (sqlException?.sqlState != null &&
                    PSQLState.UNIQUE_VIOLATION.state == sqlException.sqlState
                ) {
                    log.error(
                        "Caught unique constraint exception with sql state: {} in RestaurantApprovalRequestKafkaListener for order id: {}",
                        sqlException.sqlState,
                        restaurantApprovalRequestAvroModel.orderId
                    )
                } else {
                    throw RestaurantApplicationServiceException(
                        "Throwing DataAccessException in RestaurantApprovalRequestKafkaListener: ${e.message}", e,
                    )
                }
            } catch (_: RestaurantNotFoundException) {
                log.error(
                    "No restaurant found for restaurant id: {}, and order id: {}",
                    restaurantApprovalRequestAvroModel.restaurantId,
                    restaurantApprovalRequestAvroModel.orderId
                )
            }
        }
    }
}