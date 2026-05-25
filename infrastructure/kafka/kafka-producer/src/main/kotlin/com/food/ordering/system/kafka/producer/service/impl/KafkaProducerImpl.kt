package com.food.ordering.system.kafka.producer.service.impl

import com.food.ordering.system.kafka.producer.exception.KafkaProducerException
import com.food.ordering.system.kafka.producer.service.KafkaProducer
import org.apache.avro.specific.SpecificRecordBase
import org.apache.kafka.common.KafkaException
import org.slf4j.LoggerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.SendResult
import org.springframework.stereotype.Component
import java.io.Serializable
import java.util.concurrent.CompletableFuture

@Component
class KafkaProducerImpl<K : Serializable, V : SpecificRecordBase>(
    private val kafkaTemplate: KafkaTemplate<K, V>
) : KafkaProducer<K, V> {
    private val log = LoggerFactory.getLogger(KafkaProducerImpl::class.java)

    override fun send(
        topicName: String,
        key: K,
        message: V,
        onComplete: ((SendResult<K, V>?, Throwable?) -> Unit)?
    ): CompletableFuture<SendResult<K, V>> {
        log.info("Sending message={} to topic={}", message, topicName)

        return try {
            kafkaTemplate.send(topicName, key, message).also { future ->
                onComplete?.let { callback ->
                    future.whenComplete { result, ex -> callback(result, ex) }
                }
            }
        } catch (e: KafkaException) {
            log.error("Error on kafka producer with key: {}, message: {} and exception: {}", key, message, e.message)
            throw KafkaProducerException("Error on kafka producer with key: $key and message: $message")
        }
    }
}