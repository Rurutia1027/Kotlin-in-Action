package com.food.ordering.system.payment.service.domain.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "payment-service")
data class PaymentServiceConfigData(
    var paymentRequestTopicName: String = "",
    var paymentResponseTopicName: String = ""
)