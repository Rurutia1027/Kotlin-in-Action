package com.food.ordering.system.customer.service.domain.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "customer-service")
class CustomerServiceConfigData {
    var customerTopicName: String? = null
}