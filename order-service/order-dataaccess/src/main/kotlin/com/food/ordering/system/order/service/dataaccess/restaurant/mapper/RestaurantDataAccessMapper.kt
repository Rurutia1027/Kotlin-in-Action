package com.food.ordering.system.order.service.dataaccess.restaurant.mapper

import com.food.ordering.system.dataaccess.restaurant.entity.RestaurantEntity
import com.food.ordering.system.dataaccess.restaurant.exception.RestaurantDataAccessException
import com.food.ordering.system.domain.valueobject.Money
import com.food.ordering.system.domain.valueobject.ProductId
import com.food.ordering.system.domain.valueobject.RestaurantId
import com.food.ordering.system.order.service.domain.entity.Product
import com.food.ordering.system.order.service.domain.entity.Restaurant
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class RestaurantDataAccessMapper {

    fun restaurantToRestaurantProducts(restaurant: Restaurant): List<UUID> =
        restaurant.products.map { product -> product.id!!.value }

    fun restaurantEntityToRestaurant(restaurantEntities: List<RestaurantEntity>): Restaurant {
        val restaurantEntity = restaurantEntities.firstOrNull()
            ?: throw RestaurantDataAccessException("Restaurant could not be found!")

        val restaurantProducts = restaurantEntities.map { entity ->
            Product(
                ProductId(entity.productId!!),
                entity.productName!!,
                Money(entity.productPrice!!),
            )
        }

        return Restaurant.builder()
            .restaurantId(RestaurantId(restaurantEntity.restaurantId!!))
            .products(restaurantProducts)
            .active(restaurantEntity.restaurantActive!!)
            .build()
    }
}
