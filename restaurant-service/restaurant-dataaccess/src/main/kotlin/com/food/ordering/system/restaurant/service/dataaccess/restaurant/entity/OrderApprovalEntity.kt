package com.food.ordering.system.restaurant.service.dataaccess.restaurant.entity

import com.food.ordering.system.domain.valueobject.OrderApprovalStatus
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "order_approval", schema = "restaurant")
class OrderApprovalEntity(
    @Id
    var id: UUID? = null,
    var restaurantId: UUID? = null,
    var orderId: UUID? = null,
    @Enumerated(EnumType.STRING)
    var status: OrderApprovalStatus? = null,
)
