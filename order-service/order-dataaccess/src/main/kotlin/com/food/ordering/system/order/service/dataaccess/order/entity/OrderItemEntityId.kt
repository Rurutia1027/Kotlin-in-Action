package com.food.ordering.system.order.service.dataaccess.order.entity

import java.io.Serializable

data class OrderItemEntityId(
    var id: Long? = null,
    var order: OrderEntity? = null,
) : Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as OrderItemEntityId
        return id == that.id && order == that.order
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + (order?.hashCode() ?: 0)
        return result
    }
}
