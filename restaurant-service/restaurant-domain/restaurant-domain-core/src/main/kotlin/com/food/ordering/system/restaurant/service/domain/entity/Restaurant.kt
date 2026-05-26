package com.food.ordering.system.restaurant.service.domain.entity

import com.food.ordering.system.domain.entity.AggregateRoot
import com.food.ordering.system.domain.valueobject.Money
import com.food.ordering.system.domain.valueobject.OrderApprovalStatus
import com.food.ordering.system.domain.valueobject.OrderStatus
import com.food.ordering.system.domain.valueobject.RestaurantId
import com.food.ordering.system.restaurant.service.domain.valueobject.OrderApprovalId
import java.util.*

class Restaurant private constructor(
    val orderDetail: OrderDetail,
) : AggregateRoot<RestaurantId>() {
    var orderApproval: OrderApproval? = null
        private set
    var active: Boolean = false
        private set

    fun validateOrder(failureMessage: MutableList<String>) {
        if (orderDetail.orderStatus != OrderStatus.PAID) {
            failureMessage.add("Payment is not completed for order: ${orderDetail.id}")
        }

        val totalAmount = orderDetail.products
            .map { product ->
                if (!product.available) {
                    failureMessage.add("Product with id: ${product.id!!.value} is not available")
                }
                product.price!!.multiply(product.quantity)
            }
            .fold(Money.ZERO) { acc, money -> acc.add(money) }

        if (totalAmount != orderDetail.totalAmount) {
            failureMessage.add("Price total is not correct for order: ${orderDetail.id}")
        }
    }

    fun constructOrderApproval(orderApprovalStatus: OrderApprovalStatus) {
        orderApproval = OrderApproval.builder()
            .orderApprovalId(OrderApprovalId(UUID.randomUUID()))
            .restaurantId(id!!)
            .orderId(orderDetail.id!!)
            .approvalStatus(orderApprovalStatus)
            .build()
    }

    fun setActive(active: Boolean) {
        this.active = active
    }

    class Builder {
        private var restaurantId: RestaurantId? = null
        private var orderApproval: OrderApproval? = null
        private var active: Boolean? = null
        private var orderDetail: OrderDetail? = null

        fun restaurantId(value: RestaurantId) = apply { restaurantId = value }
        fun orderApproval(value: OrderApproval) = apply { orderApproval = value }
        fun active(value: Boolean) = apply { active = value }
        fun orderDetail(value: OrderDetail) = apply { orderDetail = value }

        fun build(): Restaurant =
            Restaurant(orderDetail = orderDetail!!).also {
                it.id = restaurantId
                it.orderApproval = orderApproval
                it.active = active ?: false
            }
    }

    companion object {
        @JvmStatic
        fun builder(): Builder = Builder()
    }
}