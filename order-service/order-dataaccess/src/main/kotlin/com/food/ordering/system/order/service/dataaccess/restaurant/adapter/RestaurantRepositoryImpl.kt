package com.food.ordering.system.order.service.dataaccess.restaurant.adapter

import com.food.ordering.system.dataaccess.restaurant.repository.RestaurantJpaRepository
import com.food.ordering.system.order.service.dataaccess.restaurant.mapper.RestaurantDataAccessMapper
import com.food.ordering.system.order.service.domain.entity.Restaurant
import com.food.ordering.system.order.service.domain.ports.output.repository.RestaurantRepository
import org.springframework.stereotype.Component
import java.util.Optional

@Component
class RestaurantRepositoryImpl(
    private val restaurantJpaRepository: RestaurantJpaRepository,
    private val restaurantDataAccessMapper: RestaurantDataAccessMapper,
) : RestaurantRepository {

    override fun findRestaurantInformation(restaurant: Restaurant): Optional<Restaurant> {
        val restaurantProducts = restaurantDataAccessMapper.restaurantToRestaurantProducts(restaurant)
        return restaurantJpaRepository
            .findByRestaurantIdAndProductIdIn(restaurant.id!!.value, restaurantProducts)
            .map(restaurantDataAccessMapper::restaurantEntityToRestaurant)
    }
}
