package com.food.ordering.system.customer.service.domain.ports.outputs.message.publisher

import com.food.ordering.system.customer.service.domain.event.CustomerCreatedEvent

interface CustomerMessagePublisher {
    fun publish(customerCreatedEvent: CustomerCreatedEvent)
}