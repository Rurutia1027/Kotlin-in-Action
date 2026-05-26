package com.food.ordering.system.restaurant.service.dataaccess.outbox.entity

import com.food.ordering.system.domain.valueobject.OrderApprovalStatus
import com.food.ordering.system.outbox.OutboxStatus
import jakarta.persistence.*
import java.time.ZonedDateTime
import java.util.*

@Entity
@Table(name = "order_outbox")
class OrderOutboxEntity(
    @Id
    var id: UUID? = null,
    var sagaId: UUID? = null,
    var createdAt: ZonedDateTime? = null,
    var processedAt: ZonedDateTime? = null,
    var type: String? = null,
    var payload: String? = null,
    @Enumerated(EnumType.STRING)
    var outboxStatus: OutboxStatus? = null,
    @Enumerated(EnumType.STRING)
    var approvalStatus: OrderApprovalStatus? = null,
    @Version
    var version: Int = 0,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as OrderOutboxEntity
        return id == that.id
    }

    override fun hashCode(): Int = id.hashCode()
}
