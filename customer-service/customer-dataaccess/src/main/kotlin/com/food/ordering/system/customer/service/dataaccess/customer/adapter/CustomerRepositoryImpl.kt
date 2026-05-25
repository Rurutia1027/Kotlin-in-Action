package com.food.ordering.system.customer.service.dataaccess.customer.adapter

import com.food.ordering.system.customer.service.dataaccess.customer.mapper.CustomerDataAccessMapper
import com.food.ordering.system.customer.service.dataaccess.customer.repository.CustomerJpaRepository
import com.food.ordering.system.customer.service.domain.entity.Customer
import com.food.ordering.system.customer.service.domain.ports.outputs.repository.CustomerRepository
import org.springframework.stereotype.Component

@Component
class CustomerRepositoryImpl(
    private val customerJpaRepository: CustomerJpaRepository,
    private val customerDataAccessMapper: CustomerDataAccessMapper
) : CustomerRepository {
    override fun createCustomer(customer: Customer): Customer =
        customerDataAccessMapper.customerEntityToCustomer(
            customerJpaRepository.save(customerDataAccessMapper.customerToCustomerEntity(customer))
        )
}