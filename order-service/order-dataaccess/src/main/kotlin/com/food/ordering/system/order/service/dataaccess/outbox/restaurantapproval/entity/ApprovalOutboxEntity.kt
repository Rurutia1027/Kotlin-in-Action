package com.food.ordering.system.order.service.dataaccess.outbox.restaurantapproval.entity

import com.food.ordering.system.domain.valueobject.OrderStatus
import com.food.ordering.system.outbox.OutboxStatus
import com.food.ordering.system.saga.SagaStatus
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.Version
import java.time.ZonedDateTime
import java.util.UUID

@Entity
@Table(name = "restaurant_approval_outbox")
class ApprovalOutboxEntity(
    @Id
    var id: UUID? = null,
    var sagaId: UUID? = null,
    var createdAt: ZonedDateTime? = null,
    var processedAt: ZonedDateTime? = null,
    var type: String? = null,
    var payload: String? = null,
    @Enumerated(EnumType.STRING)
    var sagaStatus: SagaStatus? = null,
    @Enumerated(EnumType.STRING)
    var orderStatus: OrderStatus? = null,
    @Enumerated(EnumType.STRING)
    var outboxStatus: OutboxStatus? = null,
    @Version
    var version: Int = 0,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as ApprovalOutboxEntity
        return id == that.id
    }

    override fun hashCode(): Int = id.hashCode()
}
