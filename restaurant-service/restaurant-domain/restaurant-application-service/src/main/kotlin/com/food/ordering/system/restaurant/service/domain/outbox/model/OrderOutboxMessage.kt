package com.food.ordering.system.restaurant.service.domain.outbox.model

import com.food.ordering.system.domain.valueobject.OrderApprovalStatus
import com.food.ordering.system.outbox.OutboxStatus
import java.time.ZonedDateTime
import java.util.UUID

data class OrderOutboxMessage(
val id: UUID,
    val sagaId: UUID,
    val createdAt: ZonedDateTime,
    val processedAt: ZonedDateTime,
    val type: String,
    val payload: String,
    var outboxStatus: OutboxStatus,
    val approvalStatus: OrderApprovalStatus,
    val version: Int,
) {
    companion object {
        fun builder(): Builder = Builder()
    }
    class Builder {
        private var id: UUID? = null
        private var sagaId: UUID? = null
        private var createdAt: ZonedDateTime? = null
        private var processedAt: ZonedDateTime? = null
        private var type: String? = null
        private var payload: String? = null
        private var outboxStatus: OutboxStatus? = null
        private var approvalStatus: OrderApprovalStatus? = null
        private var version: Int? = null
        fun id(value: UUID) = apply { this.id = value }
        fun sagaId(value: UUID) = apply { this.sagaId = value }
        fun createdAt(value: ZonedDateTime) = apply { this.createdAt = value }
        fun processedAt(value: ZonedDateTime) = apply { this.processedAt = value }
        fun type(value: String) = apply { this.type = value }
        fun payload(value: String) = apply { this.payload = value }
        fun outboxStatus(value: OutboxStatus) = apply { this.outboxStatus = value }
        fun approvalStatus(value: OrderApprovalStatus) = apply { this.approvalStatus = value }
        fun version(value: Int) = apply { this.version = value }
        fun build(): OrderOutboxMessage = OrderOutboxMessage(
            id = id!!,
            sagaId = sagaId!!,
            createdAt = createdAt!!,
            processedAt = processedAt!!,
            type = type!!,
            payload = payload!!,
            outboxStatus = outboxStatus!!,
            approvalStatus = approvalStatus!!,
            version = version!!
        )
    }
}
