package com.food.ordering.system.order.service.messaging.mapper

import com.food.ordering.system.domain.valueobject.OrderApprovalStatus
import com.food.ordering.system.kafka.order.avro.model.*
import com.food.ordering.system.order.service.domain.dto.message.CustomerModel
import com.food.ordering.system.order.service.domain.dto.message.PaymentResponse
import com.food.ordering.system.order.service.domain.dto.message.RestaurantApprovalResponse
import com.food.ordering.system.order.service.domain.outbox.model.approval.OrderApprovalEventPayload
import com.food.ordering.system.order.service.domain.outbox.model.payment.OrderPaymentEventPayload
import java.util.*

import com.food.ordering.system.domain.valueobject.PaymentStatus as DomainPaymentStatus

class OrderMessagingDataMapper {
    fun paymentResponseAvroModelToPaymentResponse(
        paymentResponseAvroModel: PaymentResponseAvroModel
    ): PaymentResponse =
        PaymentResponse(
            id = paymentResponseAvroModel.id.toString(),
            sagaId = paymentResponseAvroModel.sagaId.toString(),
            orderId = paymentResponseAvroModel.orderId.toString(),
            paymentId = paymentResponseAvroModel.paymentId.toString(),
            customerId = paymentResponseAvroModel.customerId.toString(),
            price = paymentResponseAvroModel.price,
            createdAt = paymentResponseAvroModel.createdAt,
            paymentStatus = DomainPaymentStatus.valueOf(paymentResponseAvroModel.paymentStatus.name),
            failureMessages = paymentResponseAvroModel.failureMessages
        )


    fun approvalResponseAvroModelToApprovalResponse(
        restaurantApprovalResponseAvroModel: RestaurantApprovalResponseAvroModel
    ): RestaurantApprovalResponse =
        RestaurantApprovalResponse(
            id = restaurantApprovalResponseAvroModel.id.toString(),
            sagaId = restaurantApprovalResponseAvroModel.sagaId.toString(),
            restaurantId = restaurantApprovalResponseAvroModel.restaurantId.toString(),
            orderId = restaurantApprovalResponseAvroModel.orderId.toString(),
            createdAt = restaurantApprovalResponseAvroModel.createdAt,
            orderApprovalStatus = OrderApprovalStatus.valueOf(restaurantApprovalResponseAvroModel.orderApprovalStatus.name),
            failureMessages = restaurantApprovalResponseAvroModel.failureMessages
        )

    fun orderPaymentEventToPaymentRequestAvroModel(
        sagaId: String,
        orderPaymentEventPayload: OrderPaymentEventPayload,
    ): PaymentRequestAvroModel =
        PaymentRequestAvroModel.newBuilder()
            .setId(UUID.randomUUID())
            .setSagaId(UUID.fromString(sagaId))
            .setCustomerId(UUID.fromString(orderPaymentEventPayload.customerId!!))
            .setOrderId(UUID.fromString(orderPaymentEventPayload.orderId!!))
            .setPrice(orderPaymentEventPayload.price)
            .setCreatedAt(orderPaymentEventPayload.createdAt!!.toInstant())
            .setPaymentOrderStatus(
                PaymentOrderStatus.valueOf(orderPaymentEventPayload.paymentOrderStatus!!),
            )
            .build()

    fun orderApprovalEventToRestaurantApprovalRequestAvroModel(
        sagaId: String,
        orderApprovalEventPayload: OrderApprovalEventPayload,
    ): RestaurantApprovalRequestAvroModel =
        RestaurantApprovalRequestAvroModel.newBuilder()
            .setId(UUID.randomUUID())
            .setSagaId(UUID.fromString(sagaId))
            .setOrderId(UUID.fromString(orderApprovalEventPayload.orderId!!))
            .setRestaurantId(UUID.fromString(orderApprovalEventPayload.restaurantId!!))
            .setRestaurantOrderStatus(
                RestaurantOrderStatus.valueOf(orderApprovalEventPayload.restaurantOrderStatus!!),
            )
            .setProducts(
                orderApprovalEventPayload.products!!.map { product ->
                    Product.newBuilder()
                        .setId(UUID.fromString(product.id!!).toString())
                        .setQuantity(product.quantity!!)
                        .build()
                },
            )
            .setPrice(orderApprovalEventPayload.price)
            .setCreatedAt(orderApprovalEventPayload.createdAt!!.toInstant())
            .build()

    fun customerAvroModeltoCustomerModel(customerAvroModel: CustomerAvroModel): CustomerModel =
        CustomerModel(
            id = customerAvroModel.id.toString(),
            username = customerAvroModel.username,
            firstName = customerAvroModel.firstName,
            lastName = customerAvroModel.lastName,
        )

}