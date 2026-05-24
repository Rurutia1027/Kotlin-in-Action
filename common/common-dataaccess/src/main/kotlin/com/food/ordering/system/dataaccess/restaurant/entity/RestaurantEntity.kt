package com.food.ordering.system.dataaccess.restaurant.entity

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.IdClass
import jakarta.persistence.Table
import java.math.BigDecimal
import java.util.*

@IdClass(RestaurantEntityId::class)
@Table(name = "order_restaurant_m_view", schema = "restaurant")
@Entity
class RestaurantEntity(
    @Id
    var restaurantId: UUID? = null,
    @Id
    var productId: UUID? = null,
    var restaurantName: String? = null,
    var restaurantActive: Boolean? = null,
    var productName: String? = null,
    var productPrice: BigDecimal? = null,
    var productAvailable: Boolean? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (this == other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as RestaurantEntity
        return restaurantId == that.restaurantId && productId == that.productId
    }

    override fun hashCode(): Int {
        var result = restaurantId?.hashCode() ?: 0
        result = 31 * result + (productId?.hashCode() ?: 0)
        return result
    }
}
