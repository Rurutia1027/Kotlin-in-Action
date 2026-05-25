package com.food.ordering.system.customer.service.domain.ports.inputs.service

import com.food.ordering.system.customer.service.domain.create.CreateCustomerCommand
import com.food.ordering.system.customer.service.domain.create.CreateCustomerResponse
import jakarta.validation.Valid

interface CustomerApplicationService {
    fun createCustomer(@Valid createCustomerCommand: CreateCustomerCommand): CreateCustomerResponse
}
