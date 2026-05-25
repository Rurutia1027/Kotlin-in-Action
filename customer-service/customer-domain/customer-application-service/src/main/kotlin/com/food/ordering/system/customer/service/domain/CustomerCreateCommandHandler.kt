package com.food.ordering.system.customer.service.domain

import com.food.ordering.system.customer.service.domain.create.CreateCustomerCommand
import com.food.ordering.system.customer.service.domain.event.CustomerCreatedEvent
import com.food.ordering.system.customer.service.domain.exception.CustomerDomainException
import com.food.ordering.system.customer.service.domain.mapper.CustomerDataMapper
import com.food.ordering.system.customer.service.domain.ports.outputs.repository.CustomerRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class CustomerCreateCommandHandler(
    private val customerDomainService: CustomerDomainService,
    private val customerRepository: CustomerRepository,
    private val customerDataMapper: CustomerDataMapper
) {
    private val log = LoggerFactory.getLogger(CustomerCreateCommandHandler::class.java)

    @Transactional
    fun createCustomer(createCustomerCommand: CreateCustomerCommand): CustomerCreatedEvent {
        val customer = customerDataMapper.createCustomerCommandToCustomer(createCustomerCommand)
        val customerCreatedEvent = customerDomainService.validateAndInitiateCustomer(customer)
        val savedCustomer = customerRepository.createCustomer(customer)
        if (savedCustomer == null) {
            log.error("Could not save customer with id: {}", createCustomerCommand.customerId)
            throw CustomerDomainException("Could not save customer with id ${createCustomerCommand.customerId}")
        }
        log.info("Returning CustomerCreatedEvent for customer id: {}", createCustomerCommand.customerId)
        return customerCreatedEvent
    }
}