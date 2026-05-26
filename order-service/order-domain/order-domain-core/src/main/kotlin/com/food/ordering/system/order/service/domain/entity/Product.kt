package com.food.ordering.system.order.service.domain.entity

import com.food.ordering.system.domain.entity.BaseEntity
import com.food.ordering.system.domain.valueobject.Money
import com.food.ordering.system.domain.valueobject.ProductId

class Product : BaseEntity<ProductId> {
    var name: String? = null
        private set
    var price: Money? = null
        private set

    constructor(productId: ProductId, name: String, price: Money) {
        id = productId
        this.name = name
        this.price = price
    }

    constructor(productId: ProductId) {
        id = productId
    }

    fun updateWithConfirmedNameAndPrice(name: String, price: Money) {
        this.name = name
        this.price = price
    }
}
