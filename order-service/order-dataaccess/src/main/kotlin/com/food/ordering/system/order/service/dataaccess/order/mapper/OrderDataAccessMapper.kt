package com.food.ordering.system.order.service.dataaccess.order.mapper

import com.food.ordering.system.domain.valueobject.*
import com.food.ordering.system.order.service.dataaccess.order.entity.OrderAddressEntity
import com.food.ordering.system.order.service.dataaccess.order.entity.OrderEntity
import com.food.ordering.system.order.service.dataaccess.order.entity.OrderItemEntity
import com.food.ordering.system.order.service.domain.entity.Order
import com.food.ordering.system.order.service.domain.entity.OrderItem
import com.food.ordering.system.order.service.domain.entity.Product
import com.food.ordering.system.order.service.domain.valueobject.OrderItemId
import com.food.ordering.system.order.service.domain.valueobject.StreetAddress
import com.food.ordering.system.order.service.domain.valueobject.TrackingId
import org.springframework.stereotype.Component

@Component
class OrderDataAccessMapper {

    fun orderToOrderEntity(order: Order): OrderEntity {
        val orderEntity = OrderEntity(
            id = order.id!!.value,
            customerId = order.customerId.value,
            restaurantId = order.restaurantId.value,
            trackingId = order.trackingId!!.value,
            address = deliveryAddressToAddressEntity(order.deliveryAddress),
            price = order.price.amount,
            items = orderItemsToOrderItemEntities(order.items).toMutableList(),
            orderStatus = order.orderStatus,
            failureMessages = order.failureMessages?.let { messages ->
                messages.joinToString(Order.FAILURE_MESSAGE_DELIMITER)
            } ?: "",
        )
        orderEntity.address!!.order = orderEntity
        orderEntity.items!!.forEach { it.order = orderEntity }
        return orderEntity
    }

    fun orderEntityToOrder(orderEntity: OrderEntity): Order =
        Order.builder()
            .orderId(OrderId(orderEntity.id!!))
            .customerId(CustomerId(orderEntity.customerId!!))
            .restaurantId(RestaurantId(orderEntity.restaurantId!!))
            .deliveryAddress(addressEntityToDeliveryAddress(orderEntity.address!!))
            .price(Money(orderEntity.price!!))
            .items(orderItemEntitiesToOrderItems(orderEntity.items!!))
            .trackingId(TrackingId(orderEntity.trackingId!!))
            .orderStatus(orderEntity.orderStatus!!)
            .failureMessages(
                if (orderEntity.failureMessages.isNullOrEmpty()) {
                    mutableListOf()
                } else {
                    orderEntity.failureMessages!!.split(Order.FAILURE_MESSAGE_DELIMITER).toMutableList()
                },
            )
            .build()

    private fun orderItemEntitiesToOrderItems(items: List<OrderItemEntity>): List<OrderItem> =
        items.map { orderItemEntity ->
            OrderItem.builder()
                .orderItemId(OrderItemId(orderItemEntity.id!!))
                .product(Product(ProductId(orderItemEntity.productId!!)))
                .price(Money(orderItemEntity.price!!))
                .quantity(orderItemEntity.quantity!!)
                .subTotal(Money(orderItemEntity.subTotal!!))
                .build()
        }

    private fun addressEntityToDeliveryAddress(address: OrderAddressEntity): StreetAddress =
        StreetAddress(
            address.id!!,
            address.street!!,
            address.postalCode!!,
            address.city!!,
        )

    private fun orderItemsToOrderItemEntities(items: List<OrderItem>): List<OrderItemEntity> =
        items.map { orderItem ->
            OrderItemEntity(
                id = orderItem.id!!.value,
                productId = orderItem.product.id!!.value,
                price = orderItem.price.amount,
                quantity = orderItem.quantity,
                subTotal = orderItem.subTotal.amount,
            )
        }

    private fun deliveryAddressToAddressEntity(deliveryAddress: StreetAddress): OrderAddressEntity =
        OrderAddressEntity(
            id = deliveryAddress.id,
            street = deliveryAddress.street,
            postalCode = deliveryAddress.postalCode,
            city = deliveryAddress.city,
        )
}
