package com.food.ordering.system.kafka.producer

import com.fasterxml.jackson.databind.ObjectMapper
import com.food.ordering.system.outbox.OutboxStatus
import org.slf4j.LoggerFactory
import org.springframework.kafka.support.SendResult
import org.springframework.stereotype.Component
import java.util.function.BiConsumer

@Component
class KafkaMessageHelper(private val objectMapper: ObjectMapper) {
    private val log = LoggerFactory.getLogger(KafkaMessageHelper::class.java)

    fun <T> getOrderEventPayload(payload: String, outputType: Class<T>): T {
        return try {
            objectMapper.readValue(payload, outputType)
        } catch (e: Exception) {
            log.error("Could not read {} object!", outputType.name, e)
            throw Exception("Could not read ${outputType.name} object!", e)
        }
    }

    fun <T, U> getKafkaCallback(
        responseTopicName: String,
        avroModel: T,
        outputMessage: U,
        outboxCallback: BiConsumer<U, OutboxStatus>,
        orderId: String,
        avroModelName: String,
    ): (SendResult<String, T>?, Throwable?) -> Unit = { result, ex ->
        handleKafkaCallback(
            ex,
            responseTopicName,
            avroModel,
            outputMessage,
            outboxCallback,
            orderId,
            avroModelName,
            result
        )
    }

    fun <T, U> handleKafkaCallback(
        ex: Throwable?,
        responseTopicName: String,
        avroModel: T,
        outboxMessage: U,
        outboxCallback: BiConsumer<U, OutboxStatus>,
        orderId: String,
        avroModelName: String,
        result: SendResult<String, T>? = null
    ) {
        if (ex != null) {
            log.error(
                "Error while sending {} with message: {} and outbox type: {} to topic {}",
                avroModelName,
                avroModel.toString(),
                outboxMessage!!::class.java.name,
                responseTopicName,
                ex,
            )
            outboxCallback.accept(outboxMessage, OutboxStatus.FAILED)
        } else {
            val metadata = result!!.recordMetadata
            log.info(
                "Received successful response from Kafka for order id: {} Topic: {} Partition: {} Offset: {} Timestamp: {}",
                orderId,
                metadata.topic(),
                metadata.partition(),
                metadata.offset(),
                metadata.timestamp()
            )
            outboxCallback.accept(outboxMessage, OutboxStatus.COMPLETED)
        }
    }
}