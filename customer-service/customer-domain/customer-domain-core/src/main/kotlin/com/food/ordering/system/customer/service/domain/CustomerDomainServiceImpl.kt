package com.food.ordering.system.customer.service.domain

import com.food.ordering.system.customer.service.domain.entity.Customer
import com.food.ordering.system.customer.service.domain.event.CustomerCreatedEvent
import com.food.ordering.system.domain.constants.DomainConstants.UTC
import org.slf4j.LoggerFactory
import java.time.ZoneId
import java.time.ZonedDateTime

class CustomerDomainServiceImpl : CustomerDomainService {
    private val log = LoggerFactory.getLogger(CustomerDomainService::class.java)

    override fun validateAndInitiateCustomer(customer: Customer): CustomerCreatedEvent {
        log.info("Customer with id: {} is initiated", customer.id!!.value)
        return CustomerCreatedEvent(customer, ZonedDateTime.now(ZoneId.of(UTC)))
    }
}