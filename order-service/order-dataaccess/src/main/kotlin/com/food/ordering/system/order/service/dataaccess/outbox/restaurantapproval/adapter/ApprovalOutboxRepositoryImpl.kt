package com.food.ordering.system.order.service.dataaccess.outbox.restaurantapproval.adapter

import com.food.ordering.system.order.service.dataaccess.outbox.restaurantapproval.exception.ApprovalOutboxNotFoundException
import com.food.ordering.system.order.service.dataaccess.outbox.restaurantapproval.mapper.ApprovalOutboxDataAccessMapper
import com.food.ordering.system.order.service.dataaccess.outbox.restaurantapproval.repository.ApprovalOutboxJpaRepository
import com.food.ordering.system.order.service.domain.outbox.model.approval.OrderApprovalOutboxMessage
import com.food.ordering.system.order.service.domain.ports.output.repository.ApprovalOutboxRepository
import com.food.ordering.system.outbox.OutboxStatus
import com.food.ordering.system.saga.SagaStatus
import org.springframework.stereotype.Component
import java.util.Optional
import java.util.UUID

@Component
class ApprovalOutboxRepositoryImpl(
    private val approvalOutboxJpaRepository: ApprovalOutboxJpaRepository,
    private val approvalOutboxDataAccessMapper: ApprovalOutboxDataAccessMapper,
) : ApprovalOutboxRepository {

    override fun save(orderApprovalOutboxMessage: OrderApprovalOutboxMessage): OrderApprovalOutboxMessage =
        approvalOutboxDataAccessMapper.approvalOutboxEntityToOrderApprovalOutboxMessage(
            approvalOutboxJpaRepository.save(
                approvalOutboxDataAccessMapper.orderCreatedOutboxMessageToOutboxEntity(orderApprovalOutboxMessage),
            ),
        )

    override fun findByTypeAndOutboxStatusAndSagaStatus(
        sagaType: String,
        outboxStatus: OutboxStatus,
        vararg sagaStatus: SagaStatus,
    ): Optional<List<OrderApprovalOutboxMessage>> {
        val entities = approvalOutboxJpaRepository
            .findByTypeAndOutboxStatusAndSagaStatusIn(sagaType, outboxStatus, sagaStatus.toList())
            .orElseThrow {
                ApprovalOutboxNotFoundException(
                    "Approval outbox object could be found for saga type $sagaType",
                )
            }
        return Optional.of(
            entities.map(approvalOutboxDataAccessMapper::approvalOutboxEntityToOrderApprovalOutboxMessage),
        )
    }

    override fun findByTypeAndSagaIdAndSagaStatus(
        type: String,
        sagaId: UUID,
        vararg sagaStatus: SagaStatus,
    ): Optional<OrderApprovalOutboxMessage> =
        approvalOutboxJpaRepository.findByTypeAndSagaIdAndSagaStatusIn(type, sagaId, sagaStatus.toList())
            .map(approvalOutboxDataAccessMapper::approvalOutboxEntityToOrderApprovalOutboxMessage)

    override fun deleteByTypeAndOutboxStatusAndSagaStatus(
        type: String,
        outboxStatus: OutboxStatus,
        vararg sagaStatus: SagaStatus,
    ) {
        approvalOutboxJpaRepository.deleteByTypeAndOutboxStatusAndSagaStatusIn(
            type,
            outboxStatus,
            sagaStatus.toList(),
        )
    }
}
