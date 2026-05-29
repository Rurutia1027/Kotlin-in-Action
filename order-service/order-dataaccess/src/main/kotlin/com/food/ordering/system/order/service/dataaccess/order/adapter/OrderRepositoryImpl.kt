package com.food.ordering.system.order.service.dataaccess.order.adapter

import com.food.ordering.system.domain.valueobject.OrderId
import com.food.ordering.system.order.service.dataaccess.order.mapper.OrderDataAccessMapper
import com.food.ordering.system.order.service.dataaccess.order.repository.OrderJpaRepository
import com.food.ordering.system.order.service.domain.entity.Order
import com.food.ordering.system.order.service.domain.ports.output.repository.OrderRepository
import com.food.ordering.system.order.service.domain.valueobject.TrackingId
import org.springframework.stereotype.Component
import java.util.Optional

@Component
class OrderRepositoryImpl(
    private val orderJpaRepository: OrderJpaRepository,
    private val orderDataAccessMapper: OrderDataAccessMapper,
) : OrderRepository {

    override fun save(order: Order): Order =
        orderDataAccessMapper.orderEntityToOrder(
            orderJpaRepository.save(orderDataAccessMapper.orderToOrderEntity(order)),
        )

    override fun findById(orderId: OrderId): Optional<Order> =
        orderJpaRepository.findById(orderId.value).map(orderDataAccessMapper::orderEntityToOrder)

    override fun findByTrackingId(trackingId: TrackingId): Optional<Order> =
        orderJpaRepository.findByTrackingId(trackingId.value)
            .map(orderDataAccessMapper::orderEntityToOrder)
}
