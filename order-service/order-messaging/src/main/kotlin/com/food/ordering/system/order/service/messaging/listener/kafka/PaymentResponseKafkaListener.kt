package com.food.ordering.system.order.service.messaging.listener.kafka

import com.food.ordering.system.kafka.consumer.KafkaConsumer
import com.food.ordering.system.kafka.order.avro.model.PaymentResponseAvroModel
import com.food.ordering.system.kafka.order.avro.model.PaymentStatus
import com.food.ordering.system.order.service.domain.exception.OrderNotFoundException
import com.food.ordering.system.order.service.domain.ports.input.message.listener.payment.PaymentResponseMessageListener
import com.food.ordering.system.order.service.messaging.mapper.OrderMessagingDataMapper
import org.slf4j.LoggerFactory
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.kafka.support.KafkaHeaders
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component

@Component
class PaymentResponseKafkaListener(
    private val paymentResponseMessageListener: PaymentResponseMessageListener,
    private val orderMessagingDataMapper: OrderMessagingDataMapper
) : KafkaConsumer<PaymentResponseAvroModel> {

    private val log = LoggerFactory.getLogger(PaymentResponseKafkaListener::class.java)

    override fun receive(
        @Payload messages: List<PaymentResponseAvroModel>,
        @Header(KafkaHeaders.RECEIVED_KEY) keys: List<String>,
        @Header(KafkaHeaders.RECEIVED_PARTITION) partitions: List<Int>,
        @Header(KafkaHeaders.OFFSET) offsets: List<Long>
    ) {
        log.info(
            "{} number of payment response received with keys: {}, partitions: {}, and offsets: {}",
            messages.size,
            keys,
            partitions,
            offsets
        )

        messages.forEach { paymentResponseAvroModel ->
            try {
                when (paymentResponseAvroModel.paymentStatus) {
                    PaymentStatus.COMPLETED -> {
                        log.info(
                            "Processing successful payment for order id: {}",
                            paymentResponseAvroModel.orderId,
                        )
                        paymentResponseMessageListener.paymentCompleted(
                            orderMessagingDataMapper.paymentResponseAvroModelToPaymentResponse(
                                paymentResponseAvroModel,
                            ),
                        )
                    }

                    PaymentStatus.CANCELLED, PaymentStatus.FAILED -> {
                        log.info(
                            "Processing unsuccessful payment for order id: {}",
                            paymentResponseAvroModel.orderId,
                        )
                        paymentResponseMessageListener.paymentCancelled(
                            orderMessagingDataMapper.paymentResponseAvroModelToPaymentResponse(
                                paymentResponseAvroModel,
                            ),
                        )
                    }

                    else -> Unit
                }
            } catch (_: OptimisticLockingFailureException) {
                log.error(
                    "Caught optimistic locking exception in PaymentResponseKafkaListener for order id: {}",
                    paymentResponseAvroModel.orderId,
                )
            } catch (_: OrderNotFoundException) {
                log.error(
                    "No order found for order id: {}",
                    paymentResponseAvroModel.orderId,
                )
            }
        }
    }
}