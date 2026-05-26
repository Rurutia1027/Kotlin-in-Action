package com.food.ordering.service.domain.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "order-service")
data class OrderServiceConfigData(
    var paymentRequestTopicName: String? = null,
    var paymentResponseTopicName: String? = null,
    var restaurantApprovalRequestTopicName: String? = null,
    var restaurantApprovalResponseTopicName: String? = null,
)
