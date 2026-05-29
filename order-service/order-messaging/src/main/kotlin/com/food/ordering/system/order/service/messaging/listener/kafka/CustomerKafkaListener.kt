package com.food.ordering.system.order.service.messaging.listener.kafka

import com.food.ordering.system.kafka.consumer.KafkaConsumer
import com.food.ordering.system.kafka.order.avro.model.CustomerAvroModel
import com.food.ordering.system.order.service.domain.ports.input.message.listener.customer.CustomerMessageListener
import com.food.ordering.system.order.service.messaging.mapper.OrderMessagingDataMapper
import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component

@Component
class CustomerKafkaListener(
    private val customerMessageListener: CustomerMessageListener,
    private val orderMessagingDataMapper: OrderMessagingDataMapper
) : KafkaConsumer<CustomerAvroModel> {

    private val log = LoggerFactory.getLogger(CustomerKafkaListener::class.java)


    @KafkaListener(
        id = "\${kafka-consumer-config.customer-group-id}",
        topics = ["\${order-service.customer-topic-name}"]
    )
    override fun receive(
        @Payload messages: List<CustomerAvroModel>,
        @Header(KafkaHeaders.RECEIVED_KEY) keys: List<String>,
        @Header(KafkaHeaders.RECEIVED_PARTITION) partitions: List<Int>,
        @Header(KafkaHeaders.OFFSET) offsets: List<Long>,
    ) {
        log.info(
            "{} number of customer create messages received with keys {}, partitions {} and offsets {}",
            messages.size,
            keys,
            partitions,
            offsets,
        )

        messages.forEach { customerAvroModel ->
            customerMessageListener.customerCreated(
                orderMessagingDataMapper.customerAvroModeltoCustomerModel(customerAvroModel),
            )
        }
    }
}