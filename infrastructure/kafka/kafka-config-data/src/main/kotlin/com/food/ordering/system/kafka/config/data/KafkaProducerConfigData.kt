package com.food.ordering.system.kafka.config.data

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "kafka-producer-config")
class KafkaProducerConfigData {
    var keySerializerClass: String? = null
    var valueSerializerClass: String? = null
    var compressionType: String? = null
    var acks: String? = null
    var batchSize: Int? = null
    var batchSizeBoostFactor: Int? = null
    var lingerMs: Int? = null
    var requestTimeoutMs: Int? = null
    var retryCount: Int? = null
}
