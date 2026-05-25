package com.food.ordering.system.payment.service.dataaccess.credithistory.entity

import com.food.ordering.system.payment.service.domain.valueobject.TransactionType
import jakarta.persistence.*
import java.math.BigDecimal
import java.util.*

@Entity
@Table(name = "credit_history")
class CreditHistoryEntity(
    @Id
    var id: UUID? = null,
    var customerId: UUID? = null,
    var amount: BigDecimal? = null,
    @Enumerated(EnumType.STRING)
    var type: TransactionType? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as CreditHistoryEntity
        return id == that.id
    }

    override fun hashCode(): Int = id.hashCode()
}
