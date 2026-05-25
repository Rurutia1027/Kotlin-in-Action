package com.food.ordering.system.payment.service.dataaccess.outbox.mapper

import OrderOutboxMessage
import com.food.ordering.system.payment.service.dataaccess.outbox.entity.OrderOutboxEntity
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
            paymentStatus = orderOutboxMessage.paymentStatus,
            version = orderOutboxMessage.version,
        )

    fun orderOutboxEntityToOrderOutboxMessage(orderOutboxEntity: OrderOutboxEntity): OrderOutboxMessage =
        OrderOutboxMessage.builder()
            .id(orderOutboxEntity.id!!)
            .sagaId(orderOutboxEntity.sagaId!!)
            .createdAt(orderOutboxEntity.createdAt!!)
            .processedAt(orderOutboxEntity.processedAt ?: orderOutboxEntity.createdAt!!)
            .type(orderOutboxEntity.type!!)
            .payload(orderOutboxEntity.payload!!)
            .outboxStatus(orderOutboxEntity.outboxStatus!!)
            .paymentStatus(orderOutboxEntity.paymentStatus!!)
            .version(orderOutboxEntity.version!!)
            .build()
}