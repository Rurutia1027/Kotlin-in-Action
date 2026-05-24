package com.food.ordering.system.dataaccess.restaurant.entity

import java.io.Serializable
import java.util.*

data class RestaurantEntityId(
    var restaurantId: UUID? = null,
    var productId: UUID? = null,
) : Serializable {
    override fun equals(other: Any?): Boolean {
        if (this == other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as RestaurantEntityId
        return restaurantId == that.restaurantId && productId == that.productId
    }

    override fun hashCode(): Int {
        var result = restaurantId?.hashCode() ?: 0
        result = 31 * result + (productId?.hashCode() ?: 0)
        return result
    }
}