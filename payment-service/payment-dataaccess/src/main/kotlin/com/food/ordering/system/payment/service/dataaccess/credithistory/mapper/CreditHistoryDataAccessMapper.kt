package com.food.ordering.system.payment.service.dataaccess.credithistory.mapper

import com.food.ordering.system.domain.valueobject.CustomerId
import com.food.ordering.system.domain.valueobject.Money
import com.food.ordering.system.payment.service.dataaccess.credithistory.entity.CreditHistoryEntity
import com.food.ordering.system.payment.service.domain.entity.CreditHistory
import com.food.ordering.system.payment.service.domain.valueobject.CreditHistoryId
import org.springframework.stereotype.Component

@Component
class CreditHistoryDataAccessMapper {

    fun creditHistoryEntityToCreditHistory(creditHistoryEntity: CreditHistoryEntity): CreditHistory =
        CreditHistory.builder()
            .creditHistoryId(CreditHistoryId(creditHistoryEntity.id!!))
            .customerId(CustomerId(creditHistoryEntity.customerId!!))
            .amount(Money(creditHistoryEntity.amount!!))
            .transactionType(creditHistoryEntity.type!!)
            .build()

    fun creditHistoryToCreditHistoryEntity(creditHistory: CreditHistory): CreditHistoryEntity =
        CreditHistoryEntity(
            id = creditHistory.id!!.value,
            customerId = creditHistory.customerId.value,
            amount = creditHistory.amount.amount,
            type = creditHistory.transactionType,
        )
}
