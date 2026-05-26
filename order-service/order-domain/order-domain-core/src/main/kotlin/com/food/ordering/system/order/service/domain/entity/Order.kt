package com.food.ordering.system.order.service.domain.entity

import com.food.ordering.system.domain.entity.AggregateRoot
import com.food.ordering.system.domain.valueobject.*
import com.food.ordering.system.order.service.domain.exception.OrderDomainException
import com.food.ordering.system.order.service.domain.valueobject.OrderItemId
import com.food.ordering.system.order.service.domain.valueobject.StreetAddress
import com.food.ordering.system.order.service.domain.valueobject.TrackingId
import java.util.*

class Order private constructor(
    val customerId: CustomerId,
    val restaurantId: RestaurantId,
    val deliveryAddress: StreetAddress,
    val price: Money,
    val items: List<OrderItem>,
) : AggregateRoot<OrderId>() {

    var trackingId: TrackingId? = null
        private set
    var orderStatus: OrderStatus? = null
        private set
    var failureMessages: MutableList<String>? = null
        private set

    fun initializeOrder() {
        id = OrderId(UUID.randomUUID())
        trackingId = TrackingId(UUID.randomUUID())
        orderStatus = (OrderStatus.PENDING)
        initializeOrderItems()
    }

    fun validateOrder() {
        validateInitialOrder()
        validateTotalPrice()
        validateItemsPrice()
    }

    fun pay() {
        if (orderStatus != OrderStatus.PENDING) {
            throw OrderDomainException("Order is not in correct state for pay operation!")
        }
        orderStatus = (OrderStatus.PAID)
    }

    fun approve() {
        if (orderStatus != OrderStatus.PAID) {
            throw OrderDomainException("Order is not in correct state for approve operation!")
        }
        orderStatus = (OrderStatus.APPROVED)
    }

    fun initCancel(failureMessages: List<String>) {
        if (orderStatus != OrderStatus.PAID) {
            throw OrderDomainException("Order is not in correct state for initCancel operation!")
        }
        orderStatus = (OrderStatus.CANCELLING)
        updateFailureMessages(failureMessages)
    }

    fun cancel(failureMessages: List<String>) {
        if (orderStatus != OrderStatus.CANCELLING && orderStatus != OrderStatus.PENDING) {
            throw OrderDomainException("Order is not in correct state for cancel operation!")
        }
        orderStatus = (OrderStatus.CANCELLED)
        updateFailureMessages(failureMessages)
    }

    private fun updateFailureMessages(failureMessages: List<String>?) {
        val filtered = failureMessages?.filter { it.isNotEmpty() }
        if (this.failureMessages != null && filtered != null) {
            this.failureMessages!!.addAll(filtered)
        } else if (this.failureMessages == null) {
            this.failureMessages = filtered?.toMutableList()
        }
    }

    private fun validateInitialOrder() {
        if (orderStatus != null || id != null) {
            throw OrderDomainException("Order is not in correct state for initialization!")
        }
    }

    private fun validateTotalPrice() {
        if (!price.isGreaterThanZero()) {
            throw OrderDomainException("Total price must be greater than zero!")
        }
    }

    private fun validateItemsPrice() {
        val orderItemsTotal = items
            .map { orderItem ->
                validateItemPrice(orderItem)
                orderItem.subTotal
            }
            .fold(Money.ZERO) { acc, subTotal -> acc.add(subTotal) }

        if (price != orderItemsTotal) {
            throw OrderDomainException(
                "Total price: ${price.amount} is not equal to Order items total: ${orderItemsTotal.amount}!",
            )
        }
    }

    private fun validateItemPrice(orderItem: OrderItem) {
        if (!orderItem.isPriceValid()) {
            throw OrderDomainException(
                "Order item price: ${orderItem.price.amount} is not valid for product ${orderItem.product.id!!.value}",
            )
        }
    }

    private fun initializeOrderItems() {
        var itemId = 1L
        for (orderItem in items) {
            orderItem.initializeOrderItem(id!!, OrderItemId(itemId++))
        }
    }

    class Builder {
        private var orderId: OrderId? = null
        private var customerId: CustomerId? = null
        private var restaurantId: RestaurantId? = null
        private var deliveryAddress: StreetAddress? = null
        private var price: Money? = null
        private var items: List<OrderItem>? = null
        private var trackingId: TrackingId? = null
        private var orderStatus: OrderStatus? = null
        private var failureMessages: List<String>? = null

        fun orderId(val_: OrderId) = apply { orderId = val_ }
        fun customerId(val_: CustomerId) = apply { customerId = val_ }
        fun restaurantId(val_: RestaurantId) = apply { restaurantId = val_ }
        fun deliveryAddress(val_: StreetAddress) = apply { deliveryAddress = val_ }
        fun price(val_: Money) = apply { price = val_ }
        fun items(val_: List<OrderItem>) = apply { items = val_ }
        fun trackingId(val_: TrackingId) = apply { trackingId = val_ }
        fun orderStatus(val_: OrderStatus) = apply { orderStatus = val_ }
        fun failureMessages(val_: List<String>) = apply { failureMessages = val_ }

        fun build(): Order {
            return Order(
                customerId = customerId!!,
                restaurantId = restaurantId!!,
                deliveryAddress = deliveryAddress!!,
                price = price!!,
                items = items!!,
            ).also {
                it.id = orderId
                it.trackingId = trackingId
                it.orderStatus = orderStatus
                it.failureMessages = failureMessages?.toMutableList()
            }
        }
    }

    companion object {
        const val FAILURE_MESSAGE_DELIMITER = ","

        @JvmStatic
        fun builder(): Builder = Builder()
    }
}
