package com.food.ordering.system.order.service.domain.ports.output.repository

import com.food.ordering.system.domain.valueobject.OrderId
import com.food.ordering.system.order.service.domain.entity.Order
import com.food.ordering.system.order.service.domain.valueobject.TrackingId
import java.util.Optional

interface OrderRepository {
    fun save(order: Order): Order

    fun findById(orderId: OrderId): Optional<Order>

    fun findByTrackingId(trackingId: TrackingId): Optional<Order>
}
