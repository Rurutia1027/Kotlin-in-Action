package com.food.ordering.system.customer.service.dataaccess.customer.mapper

import com.food.ordering.system.customer.service.dataaccess.customer.entity.CustomerEntity
import com.food.ordering.system.customer.service.domain.entity.Customer
import com.food.ordering.system.domain.valueobject.CustomerId
import org.springframework.stereotype.Component

@Component
class CustomerDataAccessMapper {
    fun customerEntityToCustomer(customerEntity: CustomerEntity): Customer =
        Customer(
            CustomerId(customerEntity.id!!),
            customerEntity.username!!,
            customerEntity.firstName!!,
            customerEntity.lastName!!
        )

    fun customerToCustomerEntity(customer: Customer): CustomerEntity =
        CustomerEntity(
            id = customer.id!!.value,
            username = customer.username,
            firstName = customer.firstName,
            lastName = customer.lastName
        )

}