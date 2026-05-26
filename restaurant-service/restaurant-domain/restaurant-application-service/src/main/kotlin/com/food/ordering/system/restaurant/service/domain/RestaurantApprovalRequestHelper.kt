package com.food.ordering.system.restaurant.service.domain

import com.food.ordering.system.domain.valueobject.OrderId
import com.food.ordering.system.outbox.OutboxStatus
import com.food.ordering.system.restaurant.service.domain.dto.RestaurantApprovalRequest
import com.food.ordering.system.restaurant.service.domain.entity.Restaurant
import com.food.ordering.system.restaurant.service.domain.exception.RestaurantNotFoundException
import com.food.ordering.system.restaurant.service.domain.mapper.RestaurantDataMapper
import com.food.ordering.system.restaurant.service.domain.outbox.scheduler.OrderOutboxHelper
import com.food.ordering.system.restaurant.service.domain.ports.outputs.message.publisher.RestaurantApprovalResponseMessagePublisher
import com.food.ordering.system.restaurant.service.domain.ports.outputs.repository.OrderApprovalRepository
import com.food.ordering.system.restaurant.service.domain.ports.outputs.repository.RestaurantRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Component
class RestaurantApprovalRequestHelper(
    private val restaurantDomainService: RestaurantDomainService,
    private val restaurantDataMapper: RestaurantDataMapper,
    private val restaurantRepository: RestaurantRepository,
    private val orderApprovalRepository: OrderApprovalRepository,
    private val orderOutboxHelper: OrderOutboxHelper,
    private val restaurantApprovalResponseMessagePublisher: RestaurantApprovalResponseMessagePublisher,
) {
    private val log = LoggerFactory.getLogger(RestaurantApprovalRequestHelper::class.java)

    @Transactional
    fun persistOrderApproval(restaurantApprovalRequest: RestaurantApprovalRequest) {
        if (publishIfOutboxMessageProcessed(restaurantApprovalRequest)) {
            log.info(
                "An outbox message with saga id: {} already saved to database!",
                restaurantApprovalRequest.sagaId
            )
            return
        }

        log.info(
            "Processing restaurant approval for order id: {}",
            restaurantApprovalRequest.orderId
        )

        val failureMessages = mutableListOf<String>()
        val restaurant = findRestaurant(restaurantApprovalRequest)
        val orderApprovalEvent = restaurantDomainService.validateOrder(restaurant, failureMessages)
        orderApprovalRepository.save(restaurant.orderApproval!!)

        orderOutboxHelper.saveOrderOutboxMessage(
            restaurantDataMapper.orderApprovalEventToOrderEventPayload(orderApprovalEvent),
            orderApprovalEvent.orderApproval.approvalStatus,
            OutboxStatus.STARTED,
            UUID.fromString(restaurantApprovalRequest.sagaId),
        )
    }

    private fun findRestaurant(restaurantApprovalRequest: RestaurantApprovalRequest): Restaurant {
        val restaurant = restaurantDataMapper.restaurantApprovalRequestToRestaurant(restaurantApprovalRequest)
        val restaurantEntity = restaurantRepository.findRestaurantInformation(restaurant)
            .orElseThrow {
                log.error("Restaurant with id ${restaurant.id!!.value} not found!")
                RestaurantNotFoundException("Restaurant with id ${restaurant.id!!.value} not found!")
            }

        restaurant.setActive(restaurantEntity.active)
        restaurant.orderDetail.products.forEach { product ->
            restaurantEntity.orderDetail.products.forEach { p ->
                if (p.id == product.id) {
                    product.updateWithConfirmedNamePriceAndAvailability(
                        p.name!!,
                        p.price!!,
                        p.available,
                    )
                }
            }
        }
        restaurant.orderDetail.setOrderId(OrderId(UUID.fromString(restaurantApprovalRequest.orderId)))
        return restaurant
    }

    private fun publishIfOutboxMessageProcessed(
        restaurantApprovalRequest: RestaurantApprovalRequest,
    ): Boolean {
        val orderOutboxMessage = orderOutboxHelper.getCompletedOrderOutboxMessageBySagaIdAndOutboxStatus(
            UUID.fromString(restaurantApprovalRequest.sagaId),
            OutboxStatus.COMPLETED
        )
        if (orderOutboxMessage.isPresent) {
            restaurantApprovalResponseMessagePublisher.publish(
                orderOutboxMessage.get(),
                orderOutboxHelper::updateOutboxStatus
            )
            return true
        }
        return false;
    }
}