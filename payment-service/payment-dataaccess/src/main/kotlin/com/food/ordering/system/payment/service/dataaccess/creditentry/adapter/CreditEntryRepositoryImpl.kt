package com.food.ordering.system.payment.service.dataaccess.creditentry.adapter

import com.food.ordering.system.domain.valueobject.CustomerId
import com.food.ordering.system.payment.service.dataaccess.creditentry.mapper.CreditEntryDataAccessMapper
import com.food.ordering.system.payment.service.dataaccess.creditentry.repository.CreditEntryJpaRepository
import com.food.ordering.system.payment.service.domain.entity.CreditEntry
import com.food.ordering.system.payment.service.domain.ports.outputs.repository.CreditEntryRepository
import org.springframework.stereotype.Component
import java.util.*

@Component
class CreditEntryRepositoryImpl(
    private val creditEntryJpaRepository: CreditEntryJpaRepository,
    private val creditEntryDataAccessMapper: CreditEntryDataAccessMapper
) : CreditEntryRepository {

    override fun save(creditEntry: CreditEntry): CreditEntry =
        creditEntryDataAccessMapper.creditEntryEntityToCreditEntry(
            creditEntryJpaRepository.save(
                creditEntryDataAccessMapper.creditEntryToCreditEntryEntity(creditEntry)
            )
        )


    override fun findByCustomerId(customerId: CustomerId): Optional<CreditEntry> =
        creditEntryJpaRepository.findByCustomerId(customerId.value)
            .map(creditEntryDataAccessMapper::creditEntryEntityToCreditEntry)
}