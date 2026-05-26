package com.food.ordering.system.order.service.domain.entity

import com.food.ordering.system.domain.entity.BaseEntity
import com.food.ordering.system.domain.valueobject.Money
import com.food.ordering.system.domain.valueobject.OrderId
import com.food.ordering.system.order.service.domain.valueobject.OrderItemId

class OrderItem private constructor(
    val product: Product,
    val quantity: Int,
    val price: Money,
    val subTotal: Money,
) : BaseEntity<OrderItemId>() {

    var orderId: OrderId? = null
        private set

    fun initializeOrderItem(orderId: OrderId, orderItemId: OrderItemId) {
        this.orderId = orderId
        id = orderItemId
    }

    fun isPriceValid(): Boolean =
        price.isGreaterThanZero() &&
                price == product.price &&
                price.multiply(quantity) == subTotal

    class Builder {
        private var orderItemId: OrderItemId? = null
        private var product: Product? = null
        private var quantity: Int = 0
        private var price: Money? = null
        private var subTotal: Money? = null

        fun orderItemId(val_: OrderItemId) = apply { orderItemId = val_ }
        fun product(val_: Product) = apply { product = val_ }
        fun quantity(val_: Int) = apply { quantity = val_ }
        fun price(val_: Money) = apply { price = val_ }
        fun subTotal(val_: Money) = apply { subTotal = val_ }

        fun build(): OrderItem =
            OrderItem(
                product = product!!,
                quantity = quantity,
                price = price!!,
                subTotal = subTotal!!,
            ).also { item ->
                orderItemId?.let { item.id = it }
            }
    }

    companion object {
        @JvmStatic
        fun builder(): Builder = Builder()
    }
}
