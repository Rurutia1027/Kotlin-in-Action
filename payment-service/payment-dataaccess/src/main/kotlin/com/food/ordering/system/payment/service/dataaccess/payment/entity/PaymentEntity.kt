package com.food.ordering.system.payment.service.dataaccess.payment.entity

import com.food.ordering.system.domain.valueobject.PaymentStatus
import jakarta.persistence.*
import java.math.BigDecimal
import java.time.ZonedDateTime
import java.util.*

@Entity
@Table(name = "payments")
class PaymentEntity(
    @Id
    var id: UUID? = null,
    var customerId: UUID? = null,
    var orderId: UUID? = null,
    var price: BigDecimal? = null,
    @Enumerated(EnumType.STRING)
    var status: PaymentStatus? = null,
    var createdAt: ZonedDateTime? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as PaymentEntity
        return id == that.id
    }

    override fun hashCode(): Int = id.hashCode()
}
