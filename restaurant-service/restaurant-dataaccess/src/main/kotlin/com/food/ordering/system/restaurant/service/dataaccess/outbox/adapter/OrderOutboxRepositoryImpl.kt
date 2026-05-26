package com.food.ordering.system.restaurant.service.dataaccess.restaurant.outbox.adapter

import com.food.ordering.system.outbox.OutboxStatus
import com.food.ordering.system.restaurant.service.dataaccess.outbox.exception.OrderOutboxNotFoundException
import com.food.ordering.system.restaurant.service.dataaccess.outbox.mapper.OrderOutboxDataAccessMapper
import com.food.ordering.system.restaurant.service.dataaccess.outbox.repository.OrderOutboxJpaRepository
import com.food.ordering.system.restaurant.service.domain.outbox.model.OrderOutboxMessage
import com.food.ordering.system.restaurant.service.domain.ports.outputs.repository.OrderOutboxRepository
import org.springframework.stereotype.Component
import java.util.*


@Component
class OrderOutboxRepositoryImpl(
    private val orderOutboxJpaRepository: OrderOutboxJpaRepository,
    private val orderOutboxDataAccessMapper: OrderOutboxDataAccessMapper,
) : OrderOutboxRepository {

    override fun save(orderOutboxMessage: OrderOutboxMessage): OrderOutboxMessage =
        orderOutboxDataAccessMapper.orderOutboxEntityToOrderOutboxMessage(
            orderOutboxJpaRepository.save(
                orderOutboxDataAccessMapper.orderOutboxMessageToOutboxEntity(orderOutboxMessage),
            ),
        )

    override fun findByTypeAndOutboxStatus(
        sagaType: String,
        outboxStatus: OutboxStatus,
    ): Optional<List<OrderOutboxMessage>> = Optional.of(
        orderOutboxJpaRepository.findByTypeAndOutboxStatus(sagaType, outboxStatus).orElseThrow {
            OrderOutboxNotFoundException(
                "Approval outbox object cannot be found for saga type $sagaType",
            )
        }.map(orderOutboxDataAccessMapper::orderOutboxEntityToOrderOutboxMessage),
    )

    override fun findByTypeAndSagaIdAndOutboxStatus(
        sagaType: String,
        sagaId: UUID,
        outboxStatus: OutboxStatus,
    ): Optional<OrderOutboxMessage> =
        orderOutboxJpaRepository.findByTypeAndSagaIdAndOutboxStatus(sagaType, sagaId, outboxStatus)
            .map(orderOutboxDataAccessMapper::orderOutboxEntityToOrderOutboxMessage)

    override fun deleteByTypeAndOutboxStatus(sagaType: String, outboxStatus: OutboxStatus) {
        orderOutboxJpaRepository.deleteByTypeAndOutboxStatus(sagaType, outboxStatus)
    }
}
