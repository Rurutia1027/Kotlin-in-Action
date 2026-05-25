package com.food.ordering.system.payment.service.dataaccess.creditentry.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.math.BigDecimal
import java.util.*

@Entity
@Table(name = "credit_entry")
class CreditEntryEntity(
    @Id
    var id: UUID? = null,
    var customerId: UUID? = null,
    var totalCreditAmount: BigDecimal? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as CreditEntryEntity
        return id == that.id
    }

    override fun hashCode(): Int = id.hashCode()
}
