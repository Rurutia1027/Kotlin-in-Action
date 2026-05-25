package com.food.ordering.system.customer.service.domain.mapper

import com.food.ordering.system.customer.service.domain.create.CreateCustomerCommand
import com.food.ordering.system.customer.service.domain.create.CreateCustomerResponse
import com.food.ordering.system.customer.service.domain.entity.Customer
import com.food.ordering.system.domain.valueobject.CustomerId
import org.springframework.stereotype.Component

@Component
class CustomerDataMapper {
    fun createCustomerCommandToCustomer(createCustomerCommand: CreateCustomerCommand): Customer =
        Customer(
            CustomerId(createCustomerCommand.customerId),
            createCustomerCommand.username,
            createCustomerCommand.firstName,
            createCustomerCommand.lastName
        )

    fun customerToCreateCustomerResponse(customer: Customer, message: String): CreateCustomerResponse =
        CreateCustomerResponse(customer.id!!.value, message)
}