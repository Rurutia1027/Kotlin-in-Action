package com.food.ordering.system.order.service.domain.outbox.model.approval

import com.food.ordering.system.domain.valueobject.OrderStatus
import com.food.ordering.system.outbox.OutboxStatus
import com.food.ordering.system.saga.SagaStatus
import java.time.ZonedDateTime
import java.util.UUID

class OrderApprovalOutboxMessage(
    var id: UUID,
    var sagaId: UUID,
    var createdAt: ZonedDateTime,
    var processedAt: ZonedDateTime? = null,
    var type: String,
    var payload: String,
    var sagaStatus: SagaStatus,
    var orderStatus: OrderStatus,
    var outboxStatus: OutboxStatus,
    var version: Int,
) {
    class Builder {
        private var id: UUID? = null
        private var sagaId: UUID? = null
        private var createdAt: ZonedDateTime? = null
        private var processedAt: ZonedDateTime? = null
        private var type: String? = null
        private var payload: String? = null
        private var sagaStatus: SagaStatus? = null
        private var orderStatus: OrderStatus? = null
        private var outboxStatus: OutboxStatus? = null
        private var version: Int = 0

        fun id(value: UUID) = apply { id = value }
        fun sagaId(value: UUID) = apply { sagaId = value }
        fun createdAt(value: ZonedDateTime) = apply { createdAt = value }
        fun processedAt(value: ZonedDateTime) = apply { processedAt = value }
        fun type(value: String) = apply { type = value }
        fun payload(value: String) = apply { payload = value }
        fun sagaStatus(value: SagaStatus) = apply { sagaStatus = value }
        fun orderStatus(value: OrderStatus) = apply { orderStatus = value }
        fun outboxStatus(value: OutboxStatus) = apply { outboxStatus = value }
        fun version(value: Int) = apply { version = value }

        fun build() = OrderApprovalOutboxMessage(
            id = id!!,
            sagaId = sagaId!!,
            createdAt = createdAt!!,
            processedAt = processedAt,
            type = type!!,
            payload = payload!!,
            sagaStatus = sagaStatus!!,
            orderStatus = orderStatus!!,
            outboxStatus = outboxStatus!!,
            version = version,
        )
    }

    companion object {
        @JvmStatic
        fun builder(): Builder = Builder()
    }
}
