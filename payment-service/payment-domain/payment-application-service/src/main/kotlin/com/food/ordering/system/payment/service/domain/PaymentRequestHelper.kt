package com.food.ordering.system.payment.service.domain

import com.food.ordering.system.domain.valueobject.CustomerId
import com.food.ordering.system.domain.valueobject.PaymentStatus
import com.food.ordering.system.outbox.OutboxStatus
import com.food.ordering.system.payment.service.domain.dto.PaymentRequest
import com.food.ordering.system.payment.service.domain.entity.CreditEntry
import com.food.ordering.system.payment.service.domain.entity.CreditHistory
import com.food.ordering.system.payment.service.domain.entity.Payment
import com.food.ordering.system.payment.service.domain.exception.PaymentApplicationServiceException
import com.food.ordering.system.payment.service.domain.exception.PaymentNotFoundException
import com.food.ordering.system.payment.service.domain.mapper.PaymentDataMapper
import com.food.ordering.system.payment.service.domain.outbox.scheduler.OrderOutboxHelper
import com.food.ordering.system.payment.service.domain.ports.outputs.message.publisher.PaymentResponseMessagePublisher
import com.food.ordering.system.payment.service.domain.ports.outputs.repository.CreditEntryRepository
import com.food.ordering.system.payment.service.domain.ports.outputs.repository.CreditHistoryRepository
import com.food.ordering.system.payment.service.domain.ports.outputs.repository.PaymentRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Component
class PaymentRequestHelper(
    private val paymentDomainService: PaymentDomainService,
    private val paymentDataMapper: PaymentDataMapper,
    private val paymentRepository: PaymentRepository,
    private val creditEntryRepository: CreditEntryRepository,
    private val creditHistoryRepository: CreditHistoryRepository,
    private val orderOutboxHelper: OrderOutboxHelper,
    private val paymentResponseMessagePublisher: PaymentResponseMessagePublisher
) {
    private val log = LoggerFactory.getLogger(PaymentRequestHelper::class.java)

    @Transactional
    fun persistPayment(paymentRequest: PaymentRequest) {
        if (publishIfOutboxMessageProcessedForPayment(paymentRequest, PaymentStatus.COMPLETED)) {
            log.info(
                "An outbox message with saga id: {} is already saved to database!",
                paymentRequest.sagaId
            )

            return
        }

        log.info("Received payment complete event for order id: {}", paymentRequest.orderId)
        val payment = paymentDataMapper.paymentRequestModelToPayment(paymentRequest)
        val creditEntry = getCreditEntry(payment.customerId)
        val creditHistories = getCreditHistory(payment.customerId).toMutableList()
        val failureMessages = mutableListOf<String>()
        val paymentEvent = paymentDomainService.validateAndInitiatePayment(
            payment,
            creditEntry,
            creditHistories,
            failureMessages
        )
        persistDbObjects(payment, creditEntry, creditHistories, failureMessages)

        orderOutboxHelper.saveOrderOutboxMessage(
            paymentDataMapper.paymentEventToOrderEventPayload(paymentEvent),
            paymentEvent.payment.paymentStatus!!,
            OutboxStatus.STARTED,
            UUID.fromString(paymentRequest.sagaId)
        )
    }

    @Transactional
    fun persistCancelPayment(paymentRequest: PaymentRequest) {
        if (publishIfOutboxMessageProcessedForPayment(paymentRequest, PaymentStatus.CANCELLED)) {
            log.info(
                "An outbox message with saga id: {} is already saved to database!",
                paymentRequest.sagaId
            )
            return
        }

        log.info("Received payment rollback event for order id: {}", paymentRequest.orderId)
        val payment = paymentRepository.findByOrderId(UUID.fromString(paymentRequest.orderId))
            .orElseThrow {
                log.error("Payment with order id: {} could not be found!", paymentRequest.orderId)
                PaymentNotFoundException("Payment with order id: ${paymentRequest.orderId} could not be found!")
            }

        val creditEntry = getCreditEntry(payment.customerId)
        val creditHistories = getCreditHistory(payment.customerId).toMutableList()
        val failureMessages = mutableListOf<String>()
        val paymentEvent = paymentDomainService.validateAndCancelPayment(
            payment,
            creditEntry,
            creditHistories,
            failureMessages
        )
        persistDbObjects(payment, creditEntry, creditHistories, failureMessages)

        orderOutboxHelper.saveOrderOutboxMessage(
            paymentDataMapper.paymentEventToOrderEventPayload(paymentEvent),
            paymentEvent.payment.paymentStatus!!,
            OutboxStatus.STARTED,
            UUID.fromString(paymentRequest.sagaId)
        )

    }

    private fun getCreditEntry(customerId: CustomerId): CreditEntry =
        creditEntryRepository.findByCustomerId(customerId).orElseThrow {
            log.error("Cloud not find credit entry for customer: {}", customerId.value)
            PaymentApplicationServiceException("Could not find credit entry for customer: ${customerId.value}")
        }

    private fun getCreditHistory(customerId: CustomerId): List<CreditHistory> =
        creditHistoryRepository.findByCustomerId(customerId).orElseThrow {
            log.error("Could not find credit history for customer: {}", customerId.value)
            PaymentApplicationServiceException("Could not find credit history for customer: ${customerId.value}")
        }

    private fun persistDbObjects(
        payment: Payment, creditEntry: CreditEntry,
        creditHistories: MutableList<CreditHistory>,
        failureMessages: MutableList<String>
    ) {
        paymentRepository.save(payment)
        if (failureMessages.isEmpty()) {
            creditEntryRepository.save(creditEntry)
            creditHistoryRepository.save(creditHistories.last())
        }
    }

    private fun publishIfOutboxMessageProcessedForPayment(
        paymentRequest: PaymentRequest,
        paymentStatus: PaymentStatus
    ): Boolean {
        val orderOutboxMessage = orderOutboxHelper.getCompletedOrderOutboxMessageBySagaIdAndPaymentStatus(
            UUID.fromString(paymentRequest.sagaId),
            paymentStatus,
        )
        if (orderOutboxMessage.isPresent) {
            paymentResponseMessagePublisher.publish(
                orderOutboxMessage.get(),
                orderOutboxHelper::updateOutboxMessage
            )
            return true
        }
        return false
    }
}