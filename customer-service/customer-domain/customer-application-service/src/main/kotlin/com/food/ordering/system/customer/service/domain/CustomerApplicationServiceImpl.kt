package com.food.ordering.system.customer.service.domain

import com.food.ordering.system.customer.service.domain.create.CreateCustomerCommand
import com.food.ordering.system.customer.service.domain.create.CreateCustomerResponse
import com.food.ordering.system.customer.service.domain.mapper.CustomerDataMapper
import com.food.ordering.system.customer.service.domain.ports.inputs.service.CustomerApplicationService
import com.food.ordering.system.customer.service.domain.ports.outputs.message.publisher.CustomerMessagePublisher
import org.springframework.stereotype.Service
import org.springframework.validation.annotation.Validated

@Service
@Validated
class CustomerApplicationServiceImpl(
    private val customerCreateCommandHandler: CustomerCreateCommandHandler,
    private val customerDataMapper: CustomerDataMapper,
    private val customerMessagePublisher: CustomerMessagePublisher
) : CustomerApplicationService {

    override fun createCustomer(createCustomerCommand: CreateCustomerCommand): CreateCustomerResponse {
        val customerCreatedEvent = customerCreateCommandHandler.createCustomer(createCustomerCommand)
        customerMessagePublisher.publish(customerCreatedEvent)
        return customerDataMapper.customerToCreateCustomerResponse(
            customerCreatedEvent.customer,
            "Customer saved successfully!",
        )
    }
}