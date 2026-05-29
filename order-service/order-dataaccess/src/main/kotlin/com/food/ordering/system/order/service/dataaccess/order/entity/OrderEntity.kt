package com.food.ordering.system.order.service.dataaccess.order.entity

import com.food.ordering.system.domain.valueobject.OrderStatus
import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.OneToOne
import jakarta.persistence.Table
import java.math.BigDecimal
import java.util.UUID

@Entity
@Table(name = "orders")
class OrderEntity(
    @Id
    var id: UUID? = null,
    var customerId: UUID? = null,
    var restaurantId: UUID? = null,
    var trackingId: UUID? = null,
    var price: BigDecimal? = null,
    @Enumerated(EnumType.STRING)
    var orderStatus: OrderStatus? = null,
    var failureMessages: String? = null,
    @OneToOne(mappedBy = "order", cascade = [CascadeType.ALL])
    var address: OrderAddressEntity? = null,
    @OneToMany(mappedBy = "order", cascade = [CascadeType.ALL])
    var items: MutableList<OrderItemEntity>? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as OrderEntity
        return id == that.id
    }

    override fun hashCode(): Int = id.hashCode()
}
