package com.food.ordering.system.customer.service.messaging.publisher.kafka

import com.food.ordering.system.customer.service.domain.config.CustomerServiceConfigData
import com.food.ordering.system.customer.service.domain.event.CustomerCreatedEvent
import com.food.ordering.system.customer.service.domain.ports.outputs.message.publisher.CustomerMessagePublisher
import com.food.ordering.system.customer.service.messaging.mapper.CustomerMessagingDataMapper
import com.food.ordering.system.kafka.order.avro.model.CustomerAvroModel
import com.food.ordering.system.kafka.producer.service.KafkaProducer
import org.apache.kafka.clients.producer.RecordMetadata
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class CustomerCreatedEventKafkaPublisher(
    private val customerMessagingDataMapper: CustomerMessagingDataMapper,
    private val kafkaProducer: KafkaProducer<String, CustomerAvroModel>,
    private val customerServiceConfigData: CustomerServiceConfigData,
) : CustomerMessagePublisher {
    private val log = LoggerFactory.getLogger(CustomerCreatedEventKafkaPublisher::class.java)
    override fun publish(customerCreatedEvent: CustomerCreatedEvent) {
        log.info(
            "Received CustomerCreatedEvent for customer id: {}",
            customerCreatedEvent.customer.id!!.value,
        )
        try {
            val customerAvroModel =
                customerMessagingDataMapper.customerCreatedEventToCustomerAvroModel(customerCreatedEvent)
            val topicName = customerServiceConfigData.customerTopicName!!

            kafkaProducer.send(
                topicName,
                customerAvroModel.id.toString(),
                customerAvroModel,
            ) { result, ex ->
                if (ex != null) {
                    log.error("Error while sending message {} to topic {}", customerAvroModel, topicName, ex)
                } else {
                    val metadata: RecordMetadata = result!!.recordMetadata
                    log.info(
                        "Received new metadata. Topic: {}; Partition {}; Offset {}; Timestamp {}, at time {}",
                        metadata.topic(),
                        metadata.partition(),
                        metadata.offset(),
                        metadata.timestamp(),
                        System.nanoTime(),
                    )
                }
            }
            log.info("CustomerCreatedEvent sent to kafka for customer id: {}", customerAvroModel.id)
        } catch (e: Exception) {
            log.error(
                "Error while sending CustomerCreatedEvent to kafka for customer id: {}, error: {}",
                customerCreatedEvent.customer.id!!.value,
                e.message
            )
        }
    }
}