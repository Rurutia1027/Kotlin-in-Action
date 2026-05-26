package com.food.ordering.system.restaurant.service.domain.entity

import com.food.ordering.system.domain.entity.BaseEntity
import com.food.ordering.system.domain.valueobject.Money
import com.food.ordering.system.domain.valueobject.ProductId

class Product private constructor(
    val quantity: Int,
) : BaseEntity<ProductId>() {
    var name: String? = null
        private set
    var price: Money? = null
        private set
    var available: Boolean = false
        private set

    fun updateWithConfirmedNamePriceAndAvailability(name: String, price: Money, available: Boolean) {
        this.name = name
        this.price = price
        this.available = available
    }

    class Builder {
        private var productId: ProductId? = null
        private var name: String? = null
        private var price: Money? = null
        private var quantity: Int? = null
        private var available: Boolean? = null

        fun productId(value: ProductId) = apply { productId = value }
        fun name(value: String) = apply { name = value }
        fun price(value: Money) = apply { price = value }
        fun quantity(value: Int) = apply { quantity = value }
        fun available(value: Boolean) = apply { available = value }

        fun build(): Product =
            Product(quantity = quantity!!).also {
                it.id = productId
                it.name = name
                it.price = price
                it.available = available ?: false
            }
    }

    companion object {
        @JvmStatic
        fun builder(): Builder = Builder()
    }
}