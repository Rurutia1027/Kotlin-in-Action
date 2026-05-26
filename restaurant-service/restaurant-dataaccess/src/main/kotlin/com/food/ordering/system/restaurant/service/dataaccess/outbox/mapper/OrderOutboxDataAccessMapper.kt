package com.food.ordering.system.restaurant.service.dataaccess.outbox.mapper

import com.food.ordering.system.restaurant.service.dataaccess.outbox.entity.OrderOutboxEntity
import com.food.ordering.system.restaurant.service.domain.outbox.model.OrderOutboxMessage
import org.springframework.stereotype.Component

@Component
class OrderOutboxDataAccessMapper {

    fun orderOutboxMessageToOutboxEntity(orderOutboxMessage: OrderOutboxMessage): OrderOutboxEntity =
        OrderOutboxEntity(
            id = orderOutboxMessage.id,
            sagaId = orderOutboxMessage.sagaId,
            createdAt = orderOutboxMessage.createdAt,
            processedAt = orderOutboxMessage.processedAt,
            type = orderOutboxMessage.type,
            payload = orderOutboxMessage.payload,
            outboxStatus = orderOutboxMessage.outboxStatus,
            approvalStatus = orderOutboxMessage.approvalStatus,
            version = orderOutboxMessage.version,
        )

    fun orderOutboxEntityToOrderOutboxMessage(orderOutboxEntity: OrderOutboxEntity): OrderOutboxMessage =
        OrderOutboxMessage.builder()
            .id(orderOutboxEntity.id!!)
            .sagaId(orderOutboxEntity.sagaId!!)
            .createdAt(orderOutboxEntity.createdAt!!)
            .processedAt(orderOutboxEntity.processedAt!!)
            .type(orderOutboxEntity.type!!)
            .payload(orderOutboxEntity.payload!!)
            .outboxStatus(orderOutboxEntity.outboxStatus!!)
            .approvalStatus(orderOutboxEntity.approvalStatus!!)
            .version(orderOutboxEntity.version!!)
            .build()
}
