package com.food.ordering.system.payment.service.domain.ports.outputs.repository

import com.food.ordering.system.payment.service.domain.entity.Payment
import java.util.*

interface PaymentRepository {
    fun save(payment: Payment): Payment
    fun findByOrderId(orderId: UUID): Optional<Payment>
}