package com.food.ordering.system.order.service.dataaccess.order.entity

import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.IdClass
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.math.BigDecimal
import java.util.UUID

@IdClass(OrderItemEntityId::class)
@Entity
@Table(name = "order_items")
class OrderItemEntity(
    @Id
    var id: Long? = null,
    @Id
    @ManyToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "ORDER_ID")
    var order: OrderEntity? = null,
    var productId: UUID? = null,
    var price: BigDecimal? = null,
    var quantity: Int? = null,
    var subTotal: BigDecimal? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as OrderItemEntity
        return id == that.id && order == that.order
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + (order?.hashCode() ?: 0)
        return result
    }
}
