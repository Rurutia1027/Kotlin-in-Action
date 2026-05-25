package com.food.ordering.system.kafka.consumer

import org.apache.avro.specific.SpecificRecordBase

interface KafkaConsumer<T : SpecificRecordBase> {
    fun receive(
        messages: @JvmSuppressWildcards List<T>,
        keys: @JvmSuppressWildcards List<String>,
        partitions: @JvmSuppressWildcards List<Int>,
        offsets: @JvmSuppressWildcards List<Long>,
    )
}
