package com.food.ordering.system.restaurant.service.domain.mapper

import com.food.ordering.system.domain.valueobject.Money
import com.food.ordering.system.domain.valueobject.OrderId
import com.food.ordering.system.domain.valueobject.OrderStatus
import com.food.ordering.system.domain.valueobject.RestaurantId
import com.food.ordering.system.restaurant.service.domain.dto.RestaurantApprovalRequest
import com.food.ordering.system.restaurant.service.domain.entity.OrderDetail
import com.food.ordering.system.restaurant.service.domain.entity.Product
import com.food.ordering.system.restaurant.service.domain.entity.Restaurant
import com.food.ordering.system.restaurant.service.domain.event.OrderApprovalEvent
import com.food.ordering.system.restaurant.service.domain.outbox.model.OrderEventPayload
import org.springframework.stereotype.Component
import java.util.*

@Component
class RestaurantDataMapper {
    fun restaurantApprovalRequestToRestaurant(
        restaurantApprovalRequest: RestaurantApprovalRequest,
    ): Restaurant =
        Restaurant.builder()
            .restaurantId(RestaurantId(UUID.fromString(restaurantApprovalRequest.restaurantId)))
            .orderDetail(
                OrderDetail.builder()
                    .orderId(OrderId(UUID.fromString(restaurantApprovalRequest.orderId)))
                    .products(
                        restaurantApprovalRequest.products.map { product ->
                            Product.builder()
                                .productId(product.id!!)
                                .quantity(product.quantity)
                                .build()
                        },
                    )
                    .totalAmount(Money(restaurantApprovalRequest.price))
                    .orderStatus(OrderStatus.valueOf(restaurantApprovalRequest.restaurantOrderStatus.name))
                    .build()
            )
            .build()

    fun orderApprovalEventToOrderEventPayload(orderApprovalEvent: OrderApprovalEvent): OrderEventPayload =
        OrderEventPayload.builder()
            .orderId(orderApprovalEvent.orderApproval.orderId.value.toString())
            .restaurantId(orderApprovalEvent.restaurantId.value.toString())
            .orderApprovalStatus(orderApprovalEvent.orderApproval.approvalStatus.name)
            .createdAt(orderApprovalEvent.createdAt)
            .failureMessages(orderApprovalEvent.failureMessages)
            .build()

}