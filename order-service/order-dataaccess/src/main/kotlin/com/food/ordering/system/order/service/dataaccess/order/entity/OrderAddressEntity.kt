package com.food.ordering.system.order.service.dataaccess.order.entity

import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "order_address")
class OrderAddressEntity(
    @Id
    var id: UUID? = null,
    @OneToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "ORDER_ID")
    var order: OrderEntity? = null,
    var street: String? = null,
    var postalCode: String? = null,
    var city: String? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as OrderAddressEntity
        return id == that.id
    }

    override fun hashCode(): Int = id.hashCode()
}
