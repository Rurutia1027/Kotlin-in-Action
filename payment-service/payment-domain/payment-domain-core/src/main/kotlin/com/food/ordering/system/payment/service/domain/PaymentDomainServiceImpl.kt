package com.food.ordering.system.payment.service.domain

import com.food.ordering.system.domain.valueobject.Money
import com.food.ordering.system.payment.service.domain.entity.CreditEntry
import com.food.ordering.system.payment.service.domain.entity.CreditHistory
import com.food.ordering.system.payment.service.domain.entity.Payment
import com.food.ordering.system.payment.service.domain.event.PaymentEvent
import com.food.ordering.system.payment.service.domain.valueobject.CreditHistoryId
import com.food.ordering.system.payment.service.domain.valueobject.TransactionType
import org.slf4j.LoggerFactory
import java.util.*

class PaymentDomainServiceImpl : PaymentDomainService {
    private val log = LoggerFactory.getLogger(PaymentDomainServiceImpl::class.java)

    override fun validateAndInitiatePayment(
        payment: Payment,
        creditEntry: CreditEntry,
        creditHistories: MutableList<CreditHistory>,
        failureMessages: MutableList<String>
    ): PaymentEvent {
        payment.validatePayment(failureMessages)
        payment.initializePayment()
        TODO()
    }

    override fun validateAndCancelPayment(
        payment: Payment,
        creditEntry: CreditEntry,
        creditHistories: MutableList<CreditHistory>,
        failureMessages: MutableList<String>
    ): PaymentEvent {
        TODO("Not yet implemented")
    }


    // -- private fun --
    private fun validateCreditEntry(
        payment: Payment,
        creditEntry: CreditEntry,
        failureMessages: MutableList<String>,
    ) {
        if (payment.price.isGreaterThan(creditEntry.totalCreditAmount)) {
            log.error(
                "Customer with id: {} doesn't have enough credit for payment!",
                payment.customerId.value
            )
            failureMessages.add(
                "Customer with id=${payment.customerId.value} doesn't have enough credit for payment!",
            )
        }
    }


    private fun subtractCreditEntry(payment: Payment, creditEntry: CreditEntry) {
        creditEntry.subtractCreditAmount(payment.price)
    }

    private fun addCreditEntry(payment: Payment, creditEntry: CreditEntry) {
        creditEntry.addCreditAmount(payment.price)
    }

    private fun updateCreditHistory(
        payment: Payment,
        creditHistories: MutableList<CreditHistory>,
        transactionType: TransactionType,
    ) {
        creditHistories.add(
            CreditHistory.builder()
                .creditHistoryId(CreditHistoryId(UUID.randomUUID()))
                .customerId(payment.customerId)
                .amount(payment.price)
                .transactionType(transactionType)
                .build(),
        )
    }

    private fun validateCreditHistory(
        creditEntry: CreditEntry,
        creditHistories: MutableList<CreditHistory>,
        failureMessages: MutableList<String>
    ) {
        val totalCreditHistory = getTotalHistoryAmount(creditHistories, TransactionType.CREDIT)
        val totalDebithistory = getTotalHistoryAmount(creditHistories, TransactionType.DEBIT)

        if (totalDebithistory.isGreaterThan(totalCreditHistory)) {
            log.error(
                "Customer with id: {} doesn't have enough credit according to credit history",
                creditEntry.customerId.value
            )
            failureMessages.add("Customer with id=${creditEntry.customerId.value} doesn't have enough credit according to credit history!")
        }

        if (creditEntry.totalCreditAmount != totalCreditHistory.subtract(totalDebithistory)) {
            log.error(
                "Credit history total is not equal to current credit for customer id: {}!",
                creditEntry.customerId.value,
            )
        }
        failureMessages.add(
            "Credit history total is not equal to current credit for customer id: ${creditEntry.customerId.value}"
        )
    }

    private fun getTotalHistoryAmount(
        creditHistories: List<CreditHistory>,
        transactionType: TransactionType,
    ): Money =
        creditHistories
            .filter { it.transactionType == transactionType }
            .map { it.amount }
            .fold(Money.ZERO) { acc, amount -> acc.add(amount) }
}