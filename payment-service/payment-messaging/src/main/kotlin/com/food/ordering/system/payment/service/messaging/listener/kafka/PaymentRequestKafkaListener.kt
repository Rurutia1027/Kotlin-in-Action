package com.food.ordering.system.payment.service.messaging.listener.kafka

import com.food.ordering.system.kafka.consumer.KafkaConsumer
import com.food.ordering.system.kafka.order.avro.model.PaymentOrderStatus
import com.food.ordering.system.kafka.order.avro.model.PaymentRequestAvroModel
import com.food.ordering.system.payment.service.domain.exception.PaymentApplicationServiceException
import com.food.ordering.system.payment.service.domain.exception.PaymentNotFoundException
import com.food.ordering.system.payment.service.domain.ports.inputs.message.listener.PaymentRequestMessageListener
import com.food.ordering.system.payment.service.messaging.mapper.PaymentMessagingDataMapper
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
class PaymentRequestKafkaListener(
    private val paymentRequestMessageListener: PaymentRequestMessageListener,
    private val paymentMessagingDataMapper: PaymentMessagingDataMapper,
) : KafkaConsumer<PaymentRequestAvroModel> {
    private val log = LoggerFactory.getLogger(PaymentRequestKafkaListener::class.java)

    @KafkaListener(
        id = "\${kafka-consumer-config.payment-customer-group-id}",
        topics = ["\${payment-service.payment-request-topic-name}"]
    )
    override fun receive(
        @Payload messages: List<PaymentRequestAvroModel>,
        @Header(KafkaHeaders.RECEIVED_KEY) keys: List<String>,
        @Header(KafkaHeaders.RECEIVED_PARTITION) partitions: List<Int>,
        @Header(KafkaHeaders.OFFSET) offsets: List<Long>
    ) {
        log.info(
            "{} number of payment requests received with keys: {}, partitions: {} and offseets: {}",
            messages.size,
            keys,
            partitions,
            offsets
        )

        messages.forEach { paymentRequestAvroModel ->

            try {
                when (paymentRequestAvroModel.paymentOrderStatus) {
                    PaymentOrderStatus.PENDING -> {
                        log.info(
                            "Processing payment for order id: {}",
                            paymentRequestAvroModel.orderId
                        )
                        paymentRequestMessageListener.completePayment(
                            paymentMessagingDataMapper.paymentRequestAvroModelToPaymentRequest(
                                paymentRequestAvroModel,
                            ),
                        )
                    }

                    PaymentOrderStatus.CANCELLED -> {
                        log.info(
                            "Cancelling payment for order id: {}",
                            paymentRequestAvroModel.orderId
                        )
                        paymentRequestMessageListener.cancelPayment(
                            paymentMessagingDataMapper.paymentRequestAvroModelToPaymentRequest(
                                paymentRequestAvroModel,
                            ),
                        )
                    }

                    else -> Unit
                }
            } catch (e: DataAccessException) {
                val sqlException = e.rootCause as? SQLException
                if (sqlException?.sqlState != null &&
                    PSQLState.UNIQUE_VIOLATION.state == sqlException.sqlState
                ) {
                    log.error(
                        "Caught unique constraint exception with sql state: {} in PaymentRequestKafkaListener for order id: {}",
                        sqlException.sqlState,
                        paymentRequestAvroModel.orderId
                    )
                } else {
                    throw PaymentApplicationServiceException(
                        "Throwing DataAccessException in PaymentRequestKafkaListener: ${e.message}",
                        e,
                    )
                }
            } catch (_: PaymentNotFoundException) {
                log.error(
                    "No payment found for order id: {}",
                    paymentRequestAvroModel.orderId
                )
            }
        }
    }
}