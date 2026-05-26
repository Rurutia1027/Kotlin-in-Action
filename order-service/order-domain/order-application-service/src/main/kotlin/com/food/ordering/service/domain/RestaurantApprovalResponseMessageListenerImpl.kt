package com.food.ordering.system.order.service.domain

import com.food.ordering.system.order.service.domain.dto.message.RestaurantApprovalResponse
import com.food.ordering.system.order.service.domain.entity.Order.Companion.FAILURE_MESSAGE_DELIMITER
import com.food.ordering.system.order.service.domain.ports.input.message.listener.restaurantapproval.RestaurantApprovalResponseMessageListener
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.validation.annotation.Validated

@Validated
@Service
class RestaurantApprovalResponseMessageListenerImpl(
    private val orderApprovalSaga: OrderApprovalSaga,
) : RestaurantApprovalResponseMessageListener {

    private val log = LoggerFactory.getLogger(RestaurantApprovalResponseMessageListenerImpl::class.java)

    override fun orderApproved(restaurantApprovalResponse: RestaurantApprovalResponse) {
        orderApprovalSaga.process(restaurantApprovalResponse)
        log.info("Order is approved for order id: {}", restaurantApprovalResponse.orderId)
    }

    override fun orderRejected(restaurantApprovalResponse: RestaurantApprovalResponse) {
        orderApprovalSaga.rollback(restaurantApprovalResponse)
        log.info(
            "Order Approval Saga rollback operation is completed for order id: {} with failure messages: {}",
            restaurantApprovalResponse.orderId,
            restaurantApprovalResponse.failureMessages?.joinToString(FAILURE_MESSAGE_DELIMITER) ?: "",
        )
    }
}
