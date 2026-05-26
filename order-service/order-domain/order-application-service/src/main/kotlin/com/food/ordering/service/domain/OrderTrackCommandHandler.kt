package com.food.ordering.system.order.service.domain

import com.food.ordering.system.order.service.domain.dto.track.TrackOrderQuery
import com.food.ordering.system.order.service.domain.dto.track.TrackOrderResponse
import com.food.ordering.system.order.service.domain.exception.OrderNotFoundException
import com.food.ordering.system.order.service.domain.mapper.OrderDataMapper
import com.food.ordering.system.order.service.domain.ports.output.repository.OrderRepository
import com.food.ordering.system.order.service.domain.valueobject.TrackingId
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class OrderTrackCommandHandler(
    private val orderDataMapper: OrderDataMapper,
    private val orderRepository: OrderRepository,
) {
    private val log = LoggerFactory.getLogger(OrderTrackCommandHandler::class.java)

    @Transactional(readOnly = true)
    fun trackOrder(trackOrderQuery: TrackOrderQuery): TrackOrderResponse {
        val orderResult = orderRepository.findByTrackingId(TrackingId(trackOrderQuery.orderTrackingId))
        if (orderResult.isEmpty) {
            log.warn("Could not find order with tracking id: {}", trackOrderQuery.orderTrackingId)
            throw OrderNotFoundException(
                "Could not find order with tracking id: ${trackOrderQuery.orderTrackingId}",
            )
        }
        return orderDataMapper.orderToTrackOrderResponse(orderResult.get())
    }
}
