package com.food.ordering.system.payment.service.domain.ports.outputs.repository

import com.food.ordering.system.domain.valueobject.CustomerId
import com.food.ordering.system.payment.service.domain.entity.CreditHistory
import java.util.*

interface CreditHistoryRepository {
    fun save(creditHistory: CreditHistory): CreditHistory
    fun findByCustomerId(customerId: CustomerId): Optional<List<CreditHistory>>
}