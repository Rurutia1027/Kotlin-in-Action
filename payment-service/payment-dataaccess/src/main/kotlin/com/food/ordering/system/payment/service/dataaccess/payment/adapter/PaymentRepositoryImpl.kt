package com.food.ordering.system.payment.service.dataaccess.payment.adapter

import com.food.ordering.system.payment.service.dataaccess.payment.mapper.PaymentDataAccessMapper
import com.food.ordering.system.payment.service.dataaccess.payment.repository.PaymentJpaRepository
import com.food.ordering.system.payment.service.domain.entity.Payment
import com.food.ordering.system.payment.service.domain.ports.outputs.repository.PaymentRepository
import org.springframework.stereotype.Component
import java.util.*

@Component
class PaymentRepositoryImpl(
    private val paymentJpaRepository: PaymentJpaRepository,
    private val paymentDataAccessMapper: PaymentDataAccessMapper,
) : PaymentRepository {

    override fun save(payment: Payment): Payment =
        paymentDataAccessMapper.paymentEntityToPayment(
            paymentJpaRepository.save(paymentDataAccessMapper.paymentToPaymentEntity(payment)),
        )

    override fun findByOrderId(orderId: UUID): Optional<Payment> =
        paymentJpaRepository.findByOrderId(orderId)
            .map(paymentDataAccessMapper::paymentEntityToPayment)
}
