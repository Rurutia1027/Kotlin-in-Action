package com.food.ordering.system.order.service.domain.entity

import com.food.ordering.system.domain.entity.AggregateRoot
import com.food.ordering.system.domain.valueobject.RestaurantId

class Restaurant private constructor(
    val products: List<Product>,
    val active: Boolean,
) : AggregateRoot<RestaurantId>() {

    class Builder {
        private var restaurantId: RestaurantId? = null
        private var products: List<Product> = emptyList()
        private var active: Boolean = false

        fun restaurantId(val_: RestaurantId) = apply { restaurantId = val_ }
        fun products(val_: List<Product>) = apply { products = val_ }
        fun active(val_: Boolean) = apply { active = val_ }

        fun build(): Restaurant {
            return Restaurant(products, active).also { it.id = restaurantId }
        }
    }

    companion object {
        @JvmStatic
        fun builder(): Builder = Builder()
    }
}
