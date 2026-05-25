package com.food.ordering.system.customer.service.messaging.mapper

import com.food.ordering.system.customer.service.domain.event.CustomerCreatedEvent
import com.food.ordering.system.kafka.order.avro.model.CustomerAvroModel
import org.springframework.stereotype.Component

@Component
class CustomerMessagingDataMapper {

    fun customerCreatedEventToCustomerAvroModel(customerCreatedEvent: CustomerCreatedEvent): CustomerAvroModel =
        CustomerAvroModel.newBuilder()
            .setId(customerCreatedEvent.customer.id!!.value)
            .setUsername(customerCreatedEvent.customer.username)
            .setFirstName(customerCreatedEvent.customer.firstName)
            .setLastName(customerCreatedEvent.customer.lastName)
            .build()
}
