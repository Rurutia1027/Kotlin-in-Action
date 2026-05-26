package com.food.ordering.system.order.service.domain.dto.track

import com.food.ordering.system.domain.valueobject.OrderStatus
import jakarta.validation.constraints.NotNull
import java.util.UUID

data class TrackOrderResponse(
    @field:NotNull val orderTrackingId: UUID,
    @field:NotNull val orderStatus: OrderStatus,
    val failureMessages: List<String>? = null,
) {
    class Builder {
        private var orderTrackingId: UUID? = null
        private var orderStatus: OrderStatus? = null
        private var failureMessages: List<String>? = null

        fun orderTrackingId(value: UUID) = apply { orderTrackingId = value }
        fun orderStatus(value: OrderStatus) = apply { orderStatus = value }
        fun failureMessages(value: List<String>?) = apply { failureMessages = value }

        fun build() = TrackOrderResponse(
            orderTrackingId = orderTrackingId!!,
            orderStatus = orderStatus!!,
            failureMessages = failureMessages,
        )
    }

    companion object {
        @JvmStatic
        fun builder(): Builder = Builder()
    }
}
