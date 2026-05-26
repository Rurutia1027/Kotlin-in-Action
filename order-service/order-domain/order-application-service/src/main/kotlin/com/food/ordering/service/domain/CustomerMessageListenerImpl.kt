package com.food.ordering.system.order.service.domain

import com.food.ordering.system.order.service.domain.dto.message.CustomerModel
import com.food.ordering.system.order.service.domain.exception.OrderDomainException
import com.food.ordering.system.order.service.domain.mapper.OrderDataMapper
import com.food.ordering.system.order.service.domain.ports.input.message.listener.customer.CustomerMessageListener
import com.food.ordering.system.order.service.domain.ports.output.repository.CustomerRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class CustomerMessageListenerImpl(
    private val customerRepository: CustomerRepository,
    private val orderDataMapper: OrderDataMapper,
) : CustomerMessageListener {

    private val log = LoggerFactory.getLogger(CustomerMessageListenerImpl::class.java)

    override fun customerCreated(customerModel: CustomerModel) {
        val customer = customerRepository.save(orderDataMapper.customerModelToCustomer(customerModel))
        if (customer == null) {
            log.error("Customer could not be created in order database with id: {}", customerModel.id)
            throw OrderDomainException(
                "Customer could not be created in order database with id ${customerModel.id}",
            )
        }
        log.info("Customer is created in order database with id: {}", customer.id)
    }
}
