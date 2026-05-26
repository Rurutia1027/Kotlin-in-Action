package com.food.ordering.system.restaurant.service.domain.ports.outputs.repository

import com.food.ordering.system.restaurant.service.domain.entity.OrderApproval

interface OrderApprovalRepository {
    fun save(orderApproval: OrderApproval): OrderApproval
}