package com.food.ordering.system.order.service.domain.mapper

import com.food.ordering.system.domain.valueobject.*
import com.food.ordering.system.order.service.domain.dto.create.CreateOrderCommand
import com.food.ordering.system.order.service.domain.dto.create.CreateOrderResponse
import com.food.ordering.system.order.service.domain.dto.create.OrderAddress
import com.food.ordering.system.order.service.domain.dto.message.CustomerModel
import com.food.ordering.system.order.service.domain.dto.track.TrackOrderResponse
import com.food.ordering.system.order.service.domain.entity.Customer
import com.food.ordering.system.order.service.domain.entity.Order
import com.food.ordering.system.order.service.domain.entity.OrderItem
import com.food.ordering.system.order.service.domain.entity.Product
import com.food.ordering.system.order.service.domain.entity.Restaurant
import com.food.ordering.system.order.service.domain.event.OrderCancelledEvent
import com.food.ordering.system.order.service.domain.event.OrderCreatedEvent
import com.food.ordering.system.order.service.domain.event.OrderPaidEvent
import com.food.ordering.system.order.service.domain.outbox.model.approval.OrderApprovalEventPayload
import com.food.ordering.system.order.service.domain.outbox.model.approval.OrderApprovalEventProduct
import com.food.ordering.system.order.service.domain.outbox.model.payment.OrderPaymentEventPayload
import com.food.ordering.system.order.service.domain.valueobject.StreetAddress
import org.springframework.stereotype.Component
import java.util.*
import com.food.ordering.system.order.service.domain.dto.create.OrderItem as CreateOrderItem

@Component
class OrderDataMapper {

    fun createOrderCommandToRestaurant(createOrderCommand: CreateOrderCommand): Restaurant =
        Restaurant.builder()
            .restaurantId(RestaurantId(createOrderCommand.restaurantId))
            .products(
                createOrderCommand.items.map { orderItem ->
                    Product(ProductId(orderItem.productId))
                },
            )
            .build()

    fun createOrderCommandToOrder(createOrderCommand: CreateOrderCommand): Order =
        Order.builder()
            .customerId(CustomerId(createOrderCommand.customerId))
            .restaurantId(RestaurantId(createOrderCommand.restaurantId))
            .deliveryAddress(orderAddressToStreetAddress(createOrderCommand.address))
            .price(Money(createOrderCommand.price))
            .items(orderItemsToOrderItemEntities(createOrderCommand.items))
            .build()

    fun orderToCreateOrderResponse(order: Order, message: String): CreateOrderResponse =
        CreateOrderResponse.builder()
            .orderTrackingId(order.trackingId!!.value)
            .orderStatus(order.orderStatus!!)
            .message(message)
            .build()

    fun orderToTrackOrderResponse(order: Order): TrackOrderResponse =
        TrackOrderResponse.builder()
            .orderTrackingId(order.trackingId!!.value)
            .orderStatus(order.orderStatus!!)
            .failureMessages(order.failureMessages)
            .build()

    fun orderCreatedEventToOrderPaymentEventPayload(orderCreatedEvent: OrderCreatedEvent): OrderPaymentEventPayload =
        OrderPaymentEventPayload.builder()
            .customerId(orderCreatedEvent.order.customerId.value.toString())
            .orderId(orderCreatedEvent.order.id!!.value.toString())
            .price(orderCreatedEvent.order.price.amount)
            .createdAt(orderCreatedEvent.createdAt)
            .paymentOrderStatus(PaymentOrderStatus.PENDING.name)
            .build()

    fun orderCancelledEventToOrderPaymentEventPayload(
        orderCancelledEvent: OrderCancelledEvent,
    ): OrderPaymentEventPayload =
        OrderPaymentEventPayload.builder()
            .customerId(orderCancelledEvent.order.customerId.value.toString())
            .orderId(orderCancelledEvent.order.id!!.value.toString())
            .price(orderCancelledEvent.order.price.amount)
            .createdAt(orderCancelledEvent.createdAt)
            .paymentOrderStatus(PaymentOrderStatus.CANCELLED.name)
            .build()

    fun orderPaidEventToOrderApprovalEventPayload(orderPaidEvent: OrderPaidEvent): OrderApprovalEventPayload =
        OrderApprovalEventPayload.builder()
            .orderId(orderPaidEvent.order.id!!.value.toString())
            .restaurantId(orderPaidEvent.order.restaurantId.value.toString())
            .restaurantOrderStatus(RestaurantOrderStatus.PAID.name)
            .products(
                orderPaidEvent.order.items.map { orderItem ->
                    OrderApprovalEventProduct.builder()
                        .id(orderItem.product.id!!.value.toString())
                        .quantity(orderItem.quantity)
                        .build()
                },
            )
            .price(orderPaidEvent.order.price.amount)
            .createdAt(orderPaidEvent.createdAt)
            .build()

    fun customerModelToCustomer(customerModel: CustomerModel): Customer =
        Customer(
            CustomerId(UUID.fromString(customerModel.id!!)),
            customerModel.username!!,
            customerModel.firstName!!,
            customerModel.lastName!!,
        )

    private fun orderItemsToOrderItemEntities(orderItems: List<CreateOrderItem>): List<OrderItem> =
        orderItems.map { orderItem ->
            OrderItem.builder()
                .product(Product(ProductId(orderItem.productId)))
                .price(Money(orderItem.price))
                .quantity(orderItem.quantity)
                .subTotal(Money(orderItem.subTotal))
                .build()
        }

    private fun orderAddressToStreetAddress(orderAddress: OrderAddress): StreetAddress =
        StreetAddress(
            UUID.randomUUID(),
            orderAddress.street,
            orderAddress.postalCode,
            orderAddress.city,
        )
}
