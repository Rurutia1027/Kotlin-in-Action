package com.food.ordering.system.payment.service.domain.ports.outputs.repository

import com.food.ordering.system.domain.valueobject.CustomerId
import com.food.ordering.system.payment.service.domain.entity.CreditEntry
import java.util.*

interface CreditEntryRepository {
    fun save(creditEntry: CreditEntry): CreditEntry
    fun findByCustomerId(customerId: CustomerId): Optional<CreditEntry>
}