package com.food.ordering.system.order.service.domain.outbox.model.approval

import com.fasterxml.jackson.annotation.JsonProperty

data class OrderApprovalEventProduct(
    @JsonProperty val id: String? = null,
    @JsonProperty val quantity: Int? = null,
) {
    class Builder {
        private var id: String? = null
        private var quantity: Int? = null

        fun id(value: String) = apply { id = value }
        fun quantity(value: Int) = apply { quantity = value }

        fun build() = OrderApprovalEventProduct(id = id, quantity = quantity)
    }

    companion object {
        @JvmStatic
        fun builder(): Builder = Builder()
    }
}
