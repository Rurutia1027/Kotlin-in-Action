package com.food.ordering.system.order.service.dataaccess.outbox.payment.adapter

import com.food.ordering.system.order.service.dataaccess.outbox.payment.exception.PaymentOutboxNotFoundException
import com.food.ordering.system.order.service.dataaccess.outbox.payment.mapper.PaymentOutboxDataAccessMapper
import com.food.ordering.system.order.service.dataaccess.outbox.payment.repository.PaymentOutboxJpaRepository
import com.food.ordering.system.order.service.domain.outbox.model.payment.OrderPaymentOutboxMessage
import com.food.ordering.system.order.service.domain.ports.output.repository.PaymentOutboxRepository
import com.food.ordering.system.outbox.OutboxStatus
import com.food.ordering.system.saga.SagaStatus
import org.springframework.stereotype.Component
import java.util.Optional
import java.util.UUID

@Component
class PaymentOutboxRepositoryImpl(
    private val paymentOutboxJpaRepository: PaymentOutboxJpaRepository,
    private val paymentOutboxDataAccessMapper: PaymentOutboxDataAccessMapper,
) : PaymentOutboxRepository {

    override fun save(orderPaymentOutboxMessage: OrderPaymentOutboxMessage): OrderPaymentOutboxMessage =
        paymentOutboxDataAccessMapper.paymentOutboxEntityToOrderPaymentOutboxMessage(
            paymentOutboxJpaRepository.save(
                paymentOutboxDataAccessMapper.orderPaymentOutboxMessageToOutboxEntity(orderPaymentOutboxMessage),
            ),
        )

    override fun findByTypeAndOutboxStatusAndSagaStatus(
        sagaType: String,
        outboxStatus: OutboxStatus,
        vararg sagaStatus: SagaStatus,
    ): Optional<List<OrderPaymentOutboxMessage>> {
        val entities = paymentOutboxJpaRepository
            .findByTypeAndOutboxStatusAndSagaStatusIn(sagaType, outboxStatus, sagaStatus.toList())
            .orElseThrow {
                PaymentOutboxNotFoundException(
                    "Payment outbox object could not be found for saga type $sagaType",
                )
            }
        return Optional.of(
            entities.map(paymentOutboxDataAccessMapper::paymentOutboxEntityToOrderPaymentOutboxMessage),
        )
    }

    override fun findByTypeAndSagaIdAndSagaStatus(
        type: String,
        sagaId: UUID,
        vararg sagaStatus: SagaStatus,
    ): Optional<OrderPaymentOutboxMessage> =
        paymentOutboxJpaRepository.findByTypeAndSagaIdAndSagaStatusIn(type, sagaId, sagaStatus.toList())
            .map(paymentOutboxDataAccessMapper::paymentOutboxEntityToOrderPaymentOutboxMessage)

    override fun deleteByTypeAndOutboxStatusAndSagaStatus(
        type: String,
        outboxStatus: OutboxStatus,
        vararg sagaStatus: SagaStatus,
    ) {
        paymentOutboxJpaRepository.deleteByTypeAndOutboxStatusAndSagaStatusIn(
            type,
            outboxStatus,
            sagaStatus.toList(),
        )
    }
}
