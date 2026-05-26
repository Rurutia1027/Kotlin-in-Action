package com.food.ordering.system.order.service.domain.dto.track

import jakarta.validation.constraints.NotNull
import java.util.*

data class TrackOrderQuery(
    @field:NotNull val orderTrackingId: UUID,
) {
    class Builder {
        private var orderTrackingId: UUID? = null

        fun orderTrackingId(value: UUID) = apply { orderTrackingId = value }

        fun build() = TrackOrderQuery(orderTrackingId = orderTrackingId!!)
    }

    companion object {
        @JvmStatic
        fun builder(): Builder = Builder()
    }
}
