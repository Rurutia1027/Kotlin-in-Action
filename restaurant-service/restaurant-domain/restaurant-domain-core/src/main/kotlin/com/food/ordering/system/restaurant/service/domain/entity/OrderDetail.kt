package com.food.ordering.system.restaurant.service.domain.entity

import com.food.ordering.system.domain.entity.BaseEntity
import com.food.ordering.system.domain.valueobject.Money
import com.food.ordering.system.domain.valueobject.OrderId
import com.food.ordering.system.domain.valueobject.OrderStatus

class OrderDetail private constructor(
    val products: List<Product>
) : BaseEntity<OrderId>(
) {
    var orderStatus: OrderStatus? = null
        private set
    var totalAmount: Money? = null
        private set

    fun setOrderId(orderId: OrderId) {
        id = orderId
    }

    class Builder {
        private var orderId: OrderId? = null
        private var orderStatus: OrderStatus? = null
        private var totalAmount: Money? = null
        private var products: List<Product>? = null

        fun orderId(value: OrderId) = apply { orderId = value }
        fun orderStatus(value: OrderStatus) = apply { orderStatus = value }
        fun totalAmount(value: Money) = apply { totalAmount = value }
        fun products(value: List<Product>) = apply { products = value }

        fun build(): OrderDetail =
            OrderDetail(products = products!!).also {
                it.id = orderId
                it.orderStatus = orderStatus
                it.totalAmount = totalAmount
            }
    }

    companion object {
        @JvmStatic
        fun builder(): Builder = Builder()
    }
}