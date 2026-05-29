package com.food.ordering.system.order.service.dataaccess.outbox.restaurantapproval.mapper

import com.food.ordering.system.order.service.dataaccess.outbox.restaurantapproval.entity.ApprovalOutboxEntity
import com.food.ordering.system.order.service.domain.outbox.model.approval.OrderApprovalOutboxMessage
import org.springframework.stereotype.Component

@Component
class ApprovalOutboxDataAccessMapper {

    fun orderCreatedOutboxMessageToOutboxEntity(
        orderApprovalOutboxMessage: OrderApprovalOutboxMessage,
    ): ApprovalOutboxEntity =
        ApprovalOutboxEntity(
            id = orderApprovalOutboxMessage.id,
            sagaId = orderApprovalOutboxMessage.sagaId,
            createdAt = orderApprovalOutboxMessage.createdAt,
            processedAt = orderApprovalOutboxMessage.processedAt,
            type = orderApprovalOutboxMessage.type,
            payload = orderApprovalOutboxMessage.payload,
            sagaStatus = orderApprovalOutboxMessage.sagaStatus,
            orderStatus = orderApprovalOutboxMessage.orderStatus,
            outboxStatus = orderApprovalOutboxMessage.outboxStatus,
            version = orderApprovalOutboxMessage.version,
        )

    fun approvalOutboxEntityToOrderApprovalOutboxMessage(
        approvalOutboxEntity: ApprovalOutboxEntity,
    ): OrderApprovalOutboxMessage =
        OrderApprovalOutboxMessage(
            id = approvalOutboxEntity.id!!,
            sagaId = approvalOutboxEntity.sagaId!!,
            createdAt = approvalOutboxEntity.createdAt!!,
            processedAt = approvalOutboxEntity.processedAt,
            type = approvalOutboxEntity.type!!,
            payload = approvalOutboxEntity.payload!!,
            sagaStatus = approvalOutboxEntity.sagaStatus!!,
            orderStatus = approvalOutboxEntity.orderStatus!!,
            outboxStatus = approvalOutboxEntity.outboxStatus!!,
            version = approvalOutboxEntity.version,
        )
}
