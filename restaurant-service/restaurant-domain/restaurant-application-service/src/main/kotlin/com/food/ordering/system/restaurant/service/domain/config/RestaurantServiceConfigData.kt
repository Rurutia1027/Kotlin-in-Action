package com.food.ordering.system.restaurant.service.domain.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "restaurant-service")
data class RestaurantServiceConfigData(
    var restaurantApprovalRequestTopicName: String = "",
    var restaurantApprovalResponseTopicName: String = ""
)