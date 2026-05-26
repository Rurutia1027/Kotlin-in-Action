package com.food.ordering.system.restaurant.service.domain.ports.outputs.repository

import com.food.ordering.system.restaurant.service.domain.entity.Restaurant
import java.util.*

interface RestaurantRepository {
    fun findRestaurantInformation(restaurant: Restaurant): Optional<Restaurant>
}
